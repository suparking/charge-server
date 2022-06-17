package net.suparking.chargeserver.charge.repository;

import net.suparking.chargeserver.car.ChargeTypeRepository;
import net.suparking.chargeserver.charge.ChargeRule;
import net.suparking.chargeserver.charge.ChargeType;
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

@Order(130)
@Repository("ChargeTypeRepositoryImpl")
public class ChargeTypeRepositoryImpl extends BasicRepositoryImpl
        implements ChargeTypeRepository, CommandLineRunner {

    private final Map<String, List<ChargeType>> chargeTypesMap = new ConcurrentHashMap<>(10);

    private final Map<String,ChargeType> defaultChargeTypeMap = new ConcurrentHashMap<>(10);

    private static final Logger log = LoggerFactory.getLogger(ChargeTypeRepositoryImpl.class);

    @Autowired
    public ChargeTypeRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<ChargeType> chargeTypes = template.findAll(ChargeType.class);
        for (ChargeType ct: chargeTypes) {
            log.info(ct.toString());
            reload(ct);
        }
    }

    @Override
    public synchronized void reload(ChargeType chargeType) {
        if (chargeType.validate()) {
            unloadById(chargeType.projectNo, chargeType.id);
            load(chargeType);
            resetDefault(chargeType.projectNo);
        } else {
            log.error("ChargeType " + chargeType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(String projectNo, ObjectId id) {
        List<ChargeType> chargeTypes = getChargeTypes(projectNo);
        if (Objects.nonNull(chargeTypes)) {
            chargeTypes.removeIf(chargeType -> chargeType.id.equals(id));
            chargeTypesMap.put(projectNo, chargeTypes);
        }
        resetDefault(projectNo);
    }

    @Override
    public synchronized ChargeType findById(String projectNo, ObjectId id) {
        List<ChargeType> ctList = getChargeTypes(projectNo);
        for (ChargeType ct: ctList) {
            if (ct.id.equals(id)) {
                return ct;
            }
        }
        log.warn("There is no ChargeType for id " + id.toString());
        return defaultChargeTypeMap.get(projectNo);
    }

    @Override
    public synchronized ChargeType findByDefault(String projectNo) {
        return defaultChargeTypeMap.get(projectNo);
    }

    @Override
    public synchronized void run(String... args) {
        log.info("ChargeType init ...");
        reloadAll();
    }

    private void load(ChargeType chargeType) {
        if (chargeTypesMap.containsKey(chargeType.projectNo)) {
            chargeTypesMap.get(chargeType.projectNo).add(chargeType);
        } else {
            List<ChargeType> chargeTypes = new LinkedList<>();
            chargeTypes.add(chargeType);
            chargeTypesMap.put(chargeType.projectNo, chargeTypes);
        }
    }

    private List<ChargeType> getChargeTypes(String projectNo) {
        return chargeTypesMap.get(projectNo);
    }
    private void resetDefault(String projectNo) {
        List<ChargeType> chargeTypes = getChargeTypes(projectNo);
        ChargeType defaultChargeType = null;
        if (Objects.nonNull(chargeTypes)) {
            for (ChargeType ct: chargeTypes) {
                if (ct.defaultType) {
                    defaultChargeType = ct;
                    break;
                }
            }
        }

        if (defaultChargeType == null) {
            defaultChargeType = new ChargeType();
            defaultChargeType.id = new ObjectId();
            defaultChargeType.chargeTypeName = "default";
            defaultChargeType.chargeRule = new ChargeRule();
            defaultChargeType.defaultType = true;
            defaultChargeType.projectNo = projectNo;
        }
        defaultChargeTypeMap.put(projectNo, defaultChargeType);
    }
}
