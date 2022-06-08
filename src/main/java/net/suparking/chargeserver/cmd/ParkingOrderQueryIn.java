package net.suparking.chargeserver.cmd;

import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.exception.ParamValidator;
import net.suparking.chargeserver.parking.Parking;
import net.suparking.chargeserver.project.ParkingConfig;
import org.bson.types.ObjectId;

public class ParkingOrderQueryIn extends ParamValidator {

    @ParamNotNull
    public ParkingConfig parkingConfig;

    @ParamNotNull
    public Parking parking;
    public DiscountInfo discountInfo;
    public ObjectId tempCarTypeId;
}
