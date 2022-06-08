package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface TimeBalanceAccountRepository {
    TimeBalanceAccount loadByCarGroup(CarGroup carGroup);
    TimeBalanceAccount loadByCarGroupId(ObjectId id);
    void save(TimeBalanceAccount timeBalanceAccount);
    List<TimeBalanceAccount> removeByCarGroupId(ObjectId id);
}
