package net.suparking.chargeserver.car.repository;

import net.suparking.chargeserver.car.CarType;
import net.suparking.chargeserver.car.CarTypeRepository;
import net.suparking.chargeserver.car.PlateNoRule;
import net.suparking.chargeserver.charge.ChargeType;
import net.suparking.chargeserver.common.CarTypeClass;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class SpecialCarType {
    private LinkedList<CarType> carTypes = new LinkedList<>();

    void removeById(ObjectId id) {
        carTypes.removeIf(carType -> carType.id.equals(id));
    }

    public void add(CarType carType) {
        carTypes.addLast(carType);
    }

    CarType match(String plateNo) {
        int min = -1;
        CarType carType = null;
        for (CarType ct: carTypes) {
            int idx = ct.matchIndex(plateNo);
            if (idx >= 0) {
                if (min < 0 || min > idx) {
                    min = idx;
                    carType = ct;
                }
            }
        }
        return carType;
    }
}

@Order(150)
@Repository("CarTypeRepositoryImpl")
public class CarTypeRepositoryImpl extends BasicRepositoryImpl implements CarTypeRepository, CommandLineRunner {
    private List<CarType> carTypes = new LinkedList<>();
    private CarType defaultCarType;
    private List<CarType> tempCarTypes = new LinkedList<>();
    private HashMap<Integer, SpecialCarType> exactTypeMap = new HashMap<>();
    private HashMap<Integer, SpecialCarType> fuzzyTypeMap = new HashMap<>();
    private List<CarType> contractedCarTypes = new LinkedList<>();

    private static final Logger log = LoggerFactory.getLogger(CarTypeRepositoryImpl.class);

    @Autowired
    public CarTypeRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<CarType> carTypes = template.findAll(CarType.class);
        for (CarType ct: carTypes) {
            log.info(ct.toString());
            reload(ct);
        }
        resetDefault();
    }

    @Override
    public synchronized void reload(CarType carType) {
        if (carType.validate()) {
            unloadById(carType.id);
            load(carType);
            resetDefault();
        } else {
            log.error("CarType " + carType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(ObjectId id) {
        carTypes.removeIf(carType -> carType.id.equals(id));
        tempCarTypes.removeIf(carType -> carType.id.equals(id));
        for (SpecialCarType specialCarType: exactTypeMap.values()) {
            specialCarType.removeById(id);
        }
        for (SpecialCarType specialCarType: fuzzyTypeMap.values()) {
            specialCarType.removeById(id);
        }
        contractedCarTypes.removeIf(carType -> carType.id.equals(id));
        resetDefault();
    }

    @Override
    public synchronized CarType findById(ObjectId id) {
        for (CarType ct: carTypes) {
            if (ct.id.equals(id)) {
                return ct;
            }
        }
        log.warn("There is no CarType entity for id " + id.toString());
        return defaultCarType;
    }

    @Override
    public CarType findByUserId(String userId) {
        if (userId.isEmpty()) {
            return defaultCarType;
        }

        CarType carType;
        SpecialCarType specialCarType;
        for (int length = userId.length(); length > 0; --length) {
            if ((specialCarType = exactTypeMap.get(length)) != null) {
                if ((carType = specialCarType.match(userId)) != null) {
                    return carType;
                }
            }
        }
        for (int length = userId.length(); length > 0; --length) {
            if ((specialCarType = fuzzyTypeMap.get(length)) != null) {
                if ((carType = specialCarType.match(userId)) != null) {
                    return carType;
                }
            }
        }

        log.warn("There is no CarType entity for [" + userId + "]");
        return defaultCarType;
    }

    @Override
    public synchronized boolean tempType(ObjectId id) {
        for (CarType ct: contractedCarTypes) {
            if (ct.id.equals(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized void run(String... arg) {
        log.info("CarType init ...");
        reloadAll();
    }

    private void load(CarType carType) {
        if (carType.carTypeClass.equals(CarTypeClass.TEMP)) {
            tempCarTypes.add(carType);
        } else if (carType.carTypeClass.equals(CarTypeClass.SPECIAL)) {
            PlateNoRule plateNoRule = carType.plateNoRule;
            if (plateNoRule.exactMatch()) {
                exactTypeMap.putIfAbsent(plateNoRule.key.length(), new SpecialCarType());
                exactTypeMap.get(plateNoRule.key.length()).add(carType);
            } else if (plateNoRule.fuzzyMatch()) {
                fuzzyTypeMap.putIfAbsent(plateNoRule.key.length(), new SpecialCarType());
                fuzzyTypeMap.get(plateNoRule.key.length()).add(carType);
            }
        } else if (carType.carTypeClass.equals(CarTypeClass.CONTRACTED)) {
            contractedCarTypes.add(carType);
        }
        carTypes.add(carType);
    }

    private void resetDefault() {
        defaultCarType = null;
        for (CarType ct: carTypes) {
            if (ct.defaultType) {
                defaultCarType = ct;
                break;
            }
        }
        if (defaultCarType == null) {
            defaultCarType = new CarType();
            defaultCarType.id = new ObjectId();
            defaultCarType.carTypeClass = CarTypeClass.TEMP;
            defaultCarType.carTypeName = "default";
            defaultCarType.carTypeAlias = "default";
            defaultCarType.defaultChargeTypeId = ChargeType.findByDefault().id;
            defaultCarType.chargeStrategies = new LinkedList<>();
            defaultCarType.defaultType = true;
        }
    }
}
