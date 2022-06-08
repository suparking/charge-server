package net.suparking.chargeserver.parking;

import net.suparking.chargeserver.car.CarContext;
import net.suparking.chargeserver.car.CarGroupTraceInfo;
import org.bson.types.ObjectId;

import static net.suparking.chargeserver.parking.EventType.EV_CAR_TYPE_CHANGE;
import static net.suparking.chargeserver.parking.EventType.EV_CHARGE_TYPE_CHANGE;
import static net.suparking.chargeserver.parking.EventType.EV_ENTER_SHARE;
import static net.suparking.chargeserver.parking.EventType.EV_ORDER_CREATE;
import static net.suparking.chargeserver.parking.EventType.EV_PASS_SHARE;

public class ParkingEvent {
    public EventType eventType;
    public Long eventTime;

    public ObjectId parkId;
    public String parkName;

    public String recogId;
    public ObjectId inSubAreaId;
    public String inSubAreaName;
    public ObjectId outSubAreaId;
    public String outSubAreaName;
    public CarGroupTraceInfo carGroupTraceInfo;

    public ParkingEvent() {}

    public ParkingEvent(ParkingEvent event) {
        this.eventType = event.eventType;
        this.eventTime = event.eventTime;
        this.parkId = event.parkId;
        this.parkName = event.parkName;
        this.recogId = event.recogId;
        this.inSubAreaId = event.inSubAreaId;
        this.inSubAreaName = event.inSubAreaName;
        this.outSubAreaId = event.outSubAreaId;
        this.outSubAreaName = event.outSubAreaName;
    }

    public ParkingEvent(ParkingTrigger parkingTrigger, EventType eventType) {
        this.eventType = eventType;
        this.eventTime = parkingTrigger.recogTime;
        this.parkId = parkingTrigger.parkId;
        this.parkName = parkingTrigger.parkName;
        this.recogId = parkingTrigger.recogId;
        this.inSubAreaId = parkingTrigger.inSubAreaId;
        this.inSubAreaName = parkingTrigger.inSubAreaName;
        this.outSubAreaId = parkingTrigger.outSubAreaId;
        this.outSubAreaName = parkingTrigger.outSubAreaName;
    }

    public ParkingEvent(long eventTime, EventType eventType, ParkingEvent event) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.parkId = event.parkId;
        this.parkName = event.parkName;
        this.inSubAreaId = this.outSubAreaId = event.inSubAreaId;
        this.inSubAreaName = this.outSubAreaName = event.inSubAreaName;
    }

    public void checkCarGroupTrace(CarContext carContext) {
        if (enableCarGroupTrace() && carContext.registered()) {
            ObjectId traceSubAreaId = inSubAreaId != null ? inSubAreaId : outSubAreaId;
            this.carGroupTraceInfo = new CarGroupTraceInfo(carContext, traceSubAreaId);
        }
    }

    public boolean parkingShared() {
        return EV_ENTER_SHARE.equals(eventType) || EV_PASS_SHARE.equals(eventType);
    }

    private boolean enableCarGroupTrace() {
        return !(EV_ORDER_CREATE.equals(eventType)
            || EV_CAR_TYPE_CHANGE.equals(eventType)
            || EV_CHARGE_TYPE_CHANGE.equals(eventType));
    }

    @Override
    public String toString() {
        return "ParkingEvent{" + "eventType=" + eventType + ", eventTime=" + eventTime + ", parkId=" + parkId +
               ", parkName='" + parkName + '\'' + ", recogId=" + recogId + ", inSubAreaId=" + inSubAreaId +
               ", inSubAreaName='" + inSubAreaName + '\'' + ", outSubAreaId=" + outSubAreaId + ", outSubAreaName='" +
               outSubAreaName + '\'' + ", carGroupTraceInfo=" + carGroupTraceInfo + '}';
    }
}
