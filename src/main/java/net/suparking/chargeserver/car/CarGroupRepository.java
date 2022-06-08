package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface CarGroupRepository {
    void save(CarGroup carGroup);
    CarGroup findByUserId(String userId);
    CarGroup findById(ObjectId id);
    List<CarGroup> findByProtocolId(ObjectId id);
}
