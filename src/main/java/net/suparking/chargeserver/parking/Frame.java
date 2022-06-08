package net.suparking.chargeserver.parking;

import org.bson.types.ObjectId;

public class Frame {
    public ParkingEvent beginEvent;
    public ParkingEvent endEvent;
    public Boolean parkingShared;
    public ObjectId subAreaId;
    public ObjectId carTypeId;
    public ObjectId chargeTypeId;

    public Frame(ParkingEvent beginEvent, ParkingEvent endEvent) {
        this.beginEvent = beginEvent;
        this.endEvent = endEvent;
    }
}