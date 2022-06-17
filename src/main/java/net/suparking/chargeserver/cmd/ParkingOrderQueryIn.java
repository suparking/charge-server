package net.suparking.chargeserver.cmd;

import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.exception.ParamValidator;
import net.suparking.chargeserver.parking.Parking;
import net.suparking.chargeserver.parking.ParkingEvent;
import net.suparking.chargeserver.parking.ParkingTrigger;
import net.suparking.chargeserver.parking.mysql.UserInfo;
import net.suparking.chargeserver.project.ParkingConfig;
import org.bson.types.ObjectId;

import java.util.LinkedList;

public class ParkingOrderQueryIn extends ParamValidator {

    @ParamNotNull
    public ParkingConfig parkingConfig;

    @ParamNotNull
    public Parking parking;

    public ParkingTrigger enter;

    public LinkedList<ParkingEvent> parkingEvents;

    public DiscountInfo discountInfo;
    public ObjectId tempCarTypeId;

    @ParamNotNull
    public UserInfo userInfo;
}
