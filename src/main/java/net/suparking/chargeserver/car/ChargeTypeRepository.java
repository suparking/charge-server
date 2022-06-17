package net.suparking.chargeserver.car;

import net.suparking.chargeserver.charge.ChargeType;
import org.bson.types.ObjectId;

public interface ChargeTypeRepository {
    void reloadAll();
    void reload(ChargeType chargeType);
    void unloadById(String projectNo, ObjectId id);
    ChargeType findById(String projectNo, ObjectId id);
    ChargeType findByDefault(String projectNo);
}
