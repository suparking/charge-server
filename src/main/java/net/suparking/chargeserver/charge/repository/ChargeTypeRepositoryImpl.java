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

@Order(130)
@Repository("ChargeTypeRepositoryImpl")
public class ChargeTypeRepositoryImpl extends BasicRepositoryImpl
        implements ChargeTypeRepository, CommandLineRunner {
    private List<ChargeType> chargeTypes = new LinkedList<>();
    private ChargeType defaultChargeType;

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
        resetDefault();
    }

    @Override
    public synchronized void reload(ChargeType chargeType) {
        if (chargeType.validate()) {
            unloadById(chargeType.id);
            load(chargeType);
            resetDefault();
        } else {
            log.error("ChargeType " + chargeType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(ObjectId id) {
        chargeTypes.removeIf(chargeType -> chargeType.id.equals(id));
        resetDefault();
    }

    @Override
    public synchronized ChargeType findById(ObjectId id) {
        List<ChargeType> ctList = chargeTypes;
        for (ChargeType ct: ctList) {
            if (ct.id.equals(id)) {
                return ct;
            }
        }
        log.warn("There is no ChargeType for id " + id.toString());
        return defaultChargeType;
    }

    @Override
    public synchronized ChargeType findByDefault() {
        return defaultChargeType;
    }

    @Override
    public synchronized void run(String... args) {
        log.info("ChargeType init ...");
        reloadAll();
    }

    private void load(ChargeType chargeType) {
        chargeTypes.add(chargeType);
    }

    private void resetDefault() {
        defaultChargeType = null;
        for (ChargeType ct: chargeTypes) {
            if (ct.defaultType) {
                defaultChargeType = ct;
                break;
            }
        }
        if (defaultChargeType == null) {
            defaultChargeType = new ChargeType();
            defaultChargeType.id = new ObjectId();
            defaultChargeType.chargeTypeName = "default";
            defaultChargeType.chargeRule = new ChargeRule();
            defaultChargeType.defaultType = true;
        }
    }
}
