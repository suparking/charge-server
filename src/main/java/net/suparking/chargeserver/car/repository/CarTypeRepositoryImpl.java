package net.suparking.chargeserver.car.repository;

import net.suparking.chargeserver.car.CarType;
import net.suparking.chargeserver.car.CarTypeRepository;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Order(150)
@Repository("CarTypeRepositoryImpl")
public class CarTypeRepositoryImpl extends BasicRepositoryImpl implements CarTypeRepository, CommandLineRunner {

    private final Map<String, List<CarType>> carTypesMap = new ConcurrentHashMap<>(10) ;

    private final Map<String, CarType> defaultCarTypeMap = new ConcurrentHashMap<>(10);
    private final Map<String, List<CarType>> tempCarTypesMap = new ConcurrentHashMap<>(10);

    private final Map<String, List<CarType>> contractedCarTypesMap = new ConcurrentHashMap<>(10);

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
    }

    @Override
    public synchronized void reload(CarType carType) {
        if (carType.validate()) {
            unloadById(carType.projectNo, carType.id);
            load(carType);
            resetDefault(carType.projectNo);
        } else {
            log.error("CarType " + carType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(String projectNo, ObjectId id) {
        List<CarType> carTypes = getCarTypes(projectNo);
        if (Objects.nonNull(carTypes)) {
            carTypes.removeIf(carType -> carType.id.equals(id));
            carTypesMap.put(projectNo, carTypes);
        }

        List<CarType> tempCarTypes = getTempCarTypes(projectNo);
        if (Objects.nonNull(tempCarTypes)) {
            tempCarTypes.removeIf(carType -> carType.id.equals(id));
            tempCarTypesMap.put(projectNo, tempCarTypes);
        }

        List<CarType> contractedCarTypes = getContractCarTypes(projectNo);
        if (Objects.nonNull(contractedCarTypes)) {
            contractedCarTypes.removeIf(carType -> carType.id.equals(id));
            contractedCarTypesMap.put(projectNo, contractedCarTypes);
        }


        resetDefault(projectNo);
    }

    @Override
    public synchronized CarType findById(String projectNo, ObjectId id) {
        List<CarType> carTypes = getCarTypes(projectNo);
        if (Objects.nonNull(carTypes)) {
            for (CarType ct: carTypes) {
                if (ct.id.equals(id)) {
                    return ct;
                }
            }
        }
        log.warn("There is no CarType entity for id " + id.toString());
        return defaultCarTypeMap.get(projectNo);
    }

    @Override
    public synchronized boolean tempType(final String projectNo, final ObjectId id) {
        List<CarType> contractedCarTypes = getContractCarTypes(projectNo);
        if (Objects.nonNull(contractedCarTypes)) {
            for (CarType ct: contractedCarTypes) {
                if (ct.id.equals(id)) {
                    return false;
                }
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
           loadTempCarType(carType);
        } else if (carType.carTypeClass.equals(CarTypeClass.CONTRACTED)) {
            loadContractedCarType(carType);
        }
        loadCarType(carType);
    }

    private void loadCarType(final CarType carType) {
        if (carTypesMap.containsKey(carType.projectNo)) {
            carTypesMap.get(carType.projectNo).add(carType);
        } else {
            List<CarType> carTypes = new LinkedList<>();
            carTypes.add(carType);
            carTypesMap.put(carType.projectNo, carTypes);
        }
    }

    private void loadTempCarType(final CarType carType) {
        if (tempCarTypesMap.containsKey(carType.projectNo)) {
            tempCarTypesMap.get(carType.projectNo).add(carType);
        } else {
            List<CarType> tempCarTypes = new LinkedList<>();
            tempCarTypes.add(carType);
            tempCarTypesMap.put(carType.projectNo, tempCarTypes);
        }
    }

    private void loadContractedCarType(final CarType carType) {
        if (contractedCarTypesMap.containsKey(carType.projectNo)) {
            contractedCarTypesMap.get(carType.projectNo).add(carType);
        } else {
            List<CarType> contractedCarTypes = new LinkedList<>();
            contractedCarTypes.add(carType);
            contractedCarTypesMap.put(carType.projectNo, contractedCarTypes);
        }
    }

    private List<CarType> getCarTypes(final String projectNo) {
        return carTypesMap.get(projectNo);
    }

    private List<CarType> getTempCarTypes(final String projectNo) {
        return tempCarTypesMap.get(projectNo);
    }

    private List<CarType> getContractCarTypes(final String projectNo) {
        return contractedCarTypesMap.get(projectNo);
    }


    private void resetDefault(final String projectNo) {
        CarType defaultCarType = null;
        List<CarType> carTypes = getCarTypes(projectNo);
        if (Objects.nonNull(carTypes)) {
            for (CarType ct: carTypes) {
                if (ct.defaultType) {
                    defaultCarType = ct;
                    break;
                }
            }
        }

        if (defaultCarType == null) {
            defaultCarType = new CarType();
            defaultCarType.id = new ObjectId();
            defaultCarType.carTypeClass = CarTypeClass.TEMP;
            defaultCarType.carTypeName = "default";
            defaultCarType.carTypeAlias = "default";
            defaultCarType.defaultChargeTypeId = ChargeType.findByDefault(projectNo).id;
            defaultCarType.chargeStrategies = new LinkedList<>();
            defaultCarType.defaultType = true;
            defaultCarType.projectNo = projectNo;
        }
        defaultCarTypeMap.put(projectNo, defaultCarType);
    }
}
