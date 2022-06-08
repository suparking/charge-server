package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

public class ChargeStrategy {
    public Boolean multiParking;
    public ObjectId subAreaId;
    public ObjectId dateTypeId;
    public ObjectId chargeTypeId;

    @Override
    public String toString() {
        return "ChargeStrategy{" + "parkingShared=" + multiParking + ", subAreaId='" + subAreaId + '\'' + ", dateTypeId='" +
               dateTypeId + '\'' + ", chargeTypeId='" + chargeTypeId + '\'' + '}';
    }
}
