package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface CarGroupRepository {
    CarGroup findByProjectNoAndUserId(String projectNo, String userId);
    CarGroup findByProjectNoAndId(String projectNo, ObjectId id);
    List<CarGroup> findByProjectNoAndProtocolId(String projectNo, ObjectId id);
}
