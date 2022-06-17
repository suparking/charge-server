package net.suparking.chargeserver.parking;

import net.suparking.chargeserver.util.Util;

public class HistoryPayment {
    public String userId;
    public String begin;
    public String end;
    public Integer effectiveAmount;

    public HistoryPayment() {}

    public HistoryPayment(ParkingOrder order) {
        this.userId = order.userId;
        this.begin = Util.epochToYMDHMS(order.beginTime);
        this.end = Util.epochToYMDHMS(order.endTime);
        this.effectiveAmount = order.effectiveAmount();
    }

    public HistoryPayment(ParkingOrder order, long beginTime, long endTime) {
        this.userId = order.userId;
        this.begin = Util.epochToYMDHMS(order.beginTime);
        this.end = Util.epochToYMDHMS(order.endTime);
        this.effectiveAmount = order.effectiveAmount(beginTime, endTime);
    }

    @Override
    public String toString() {
        return "HistoryPayment{" + "userId='" + userId + '\'' + ", begin='" + begin + '\'' + ", end='" + end + '\'' +
               ", effectiveAmount=" + effectiveAmount + '}';
    }
}
