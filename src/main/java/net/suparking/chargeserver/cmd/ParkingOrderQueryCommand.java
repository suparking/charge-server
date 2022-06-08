package net.suparking.chargeserver.cmd;

import net.suparking.chargeserver.common.DiscountInfo;
import net.suparking.chargeserver.exception.ServerException;
import net.suparking.chargeserver.parking.Park;
import net.suparking.chargeserver.parking.Parking;
import net.suparking.chargeserver.parking.ParkingOrder;
import net.suparking.chargeserver.project.ParkingConfig;
import net.suparking.chargeserver.util.Util;
import org.springframework.stereotype.Component;

import static net.suparking.chargeserver.exception.ErrorCode.BIZ_INVALID_PARKING_STATE;
import static net.suparking.chargeserver.exception.ErrorCode.EXCEPTION_PARKING_ID_NOT_EXIST;

@Component("PARKING_ORDER_QUERY")
public class ParkingOrderQueryCommand {

    /**
     * TODO: 2 -->  费用计算入口
     * @param in
     * @return
     */
    public ParkingOrderQueryOut execute(ParkingOrderQueryIn in) {
        Parking parking = in.parking;
        if (parking == null) {
            throw new ServerException(EXCEPTION_PARKING_ID_NOT_EXIST);
        }

        ParkingOrder parkingOrder;
        if (parking.waitingForPay()) {
            // TODO: 优惠劵种类只能同时使用一种
            boolean update = parking.pendingOrder.expired()
                             || !DiscountInfo.same(parking.pendingOrder.discountInfo, in.discountInfo);
            if (update) {
                parkingOrder = parking.queryOrder(in.discountInfo, in.tempCarTypeId);
                parking.pendingOrder = parkingOrder;


                Park park = Park.builder()
                        .parkNo(parking.getParkNo())
                        .parkName(parking.getParkName())
                        .deviceNo(parking.getDeviceNo())
                        .build();
                parking.logTrace(park, "出场费用查询￥" + Util.RMBFenToYuan(parkingOrder.dueAmount));
            } else {
                parkingOrder = parking.pendingOrder;
            }
        } else if (parking.entered()) {
            parkingOrder = parking.queryOrder(in.discountInfo, null);
            parking.logTrace(null, "场内费用查询￥" + Util.RMBFenToYuan(parkingOrder.dueAmount));
            ParkingConfig parkingConfig = in.parkingConfig;
            parkingOrder.bestBefore = parkingConfig.prepayFreeMinutes * Util.minuteSeconds + parkingOrder.endTime;
        } else {
            throw new ServerException(BIZ_INVALID_PARKING_STATE);
        }

        ParkingOrderQueryOut out = new ParkingOrderQueryOut();
        out.parkingOrder = parkingOrder;
        return out;
    }
}
