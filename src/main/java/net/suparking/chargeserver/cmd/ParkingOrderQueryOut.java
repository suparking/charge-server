package net.suparking.chargeserver.cmd;

import net.suparking.chargeserver.parking.ParkingOrder;

import static net.suparking.chargeserver.exception.ErrorCode.SUCCESS;

public class ParkingOrderQueryOut {
    public String code = SUCCESS;
    public ParkingOrder parkingOrder;
    public ParkingOrderQueryOut() {}
    public ParkingOrderQueryOut(String code) {
        this.code = code;
    }
}
