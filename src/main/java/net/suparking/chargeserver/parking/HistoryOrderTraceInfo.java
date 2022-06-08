package net.suparking.chargeserver.parking;

import net.suparking.server.util.Util;

import java.util.LinkedList;

public class HistoryOrderTraceInfo {
    public String begin;
    public String end;
    public Integer unitCount;
    public Integer maxAmount;
    public Integer historyEffectiveAmount = 0;
    public LinkedList<HistoryPayment> historyPayments = new LinkedList<>();

    public HistoryOrderTraceInfo() {}

    public HistoryOrderTraceInfo(long begin, long end, int unitAmount, int unitMinutes) {
        this.begin = Util.epochToYMDHMS(begin);
        this.end = Util.epochToYMDHMS(end);
        int totalRangeMinutes = Util.timeGapToMinute(begin, end);
        this.unitCount = Util.lengthToUnit(totalRangeMinutes, unitMinutes);
        this.maxAmount = unitAmount * this.unitCount;
    }

    public void addPayment(HistoryPayment payment) {
        historyEffectiveAmount += payment.effectiveAmount;
        historyPayments.addLast(payment);
    }

    public Integer maxAvailableAmount() {
        return maxAmount - Integer.min(maxAmount, historyEffectiveAmount);
    }

    @Override
    public String toString() {
        return "HistoryOrderTraceInfo{" + "begin='" + begin + '\'' + ", end='" + end + '\'' + ", unitCount=" +
               unitCount + ", maxAmount=" + maxAmount + ", historyEffectiveAmount=" + historyEffectiveAmount +
               ", historyPayments=" + historyPayments + '}';
    }
}
