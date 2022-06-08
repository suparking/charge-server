package net.suparking.chargeserver.car;

import net.suparking.chargeserver.charge.ChargeType;
import org.bson.types.ObjectId;

public interface ChargeTypeRepository {
    void reloadAll();
    void reload(ChargeType chargeType);
    void unloadById(ObjectId id);
    ChargeType findById(ObjectId id);
    ChargeType findByDefault();
}
