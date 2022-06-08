package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.Map;

public class CarGroupTraceInfo {
    public Integer leftDay;
    public Integer spaceQuantity;

    public CarGroupTraceInfo() {}

    public CarGroupTraceInfo(CarContext carContext, ObjectId subAreaId) {
        this.leftDay = carContext.leftDay();
        this.spaceQuantity = carContext.spaceQuantity(subAreaId);
    }

    @Override
    public String toString() {
        return "CarGroupTraceInfo{" + "leftDay=" + leftDay + ", spaceQuantity=" + spaceQuantity + '}';
    }
}
