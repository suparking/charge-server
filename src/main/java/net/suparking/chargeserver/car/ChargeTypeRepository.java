package net.suparking.chargeserver.car;

import net.suparking.chargeserver.charge.ChargeType;
import org.bson.types.ObjectId;

import java.util.List;

public interface ChargeTypeRepository {
    void reloadAll();
    void reload(ChargeType chargeType);
    void unloadById(String projectNo, ObjectId id);
    ChargeType findById(String projectNo, ObjectId id);
    ChargeType findByDefault(String projectNo);

    void reloadByProjectNo(String projectNo);
    List<ChargeType> findByProjectNo(String projectNo);
}
