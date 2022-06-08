package net.suparking.chargeserver.charge;

import org.bson.types.ObjectId;

public class ChargePeriod {
    public Long beginTime;
    public Long endTime;
    public ObjectId chargeTypeId;

    public ChargePeriod(Long beginTime, Long endTime, ObjectId chargeTypeId) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.chargeTypeId = chargeTypeId;
    }
}
