package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.util.Util;

import java.util.LinkedList;

public class ChargeInfo {
    public Integer beginCycleSeq;
    public Integer cycleNumber;
    public LinkedList<ChargeDetail> chargeDetails;
    public Integer parkingMinutes;
    public Integer balancedMinutes;
    public Integer discountedMinutes;
    public Integer totalAmount;
    public Integer extraAmount;

    public ChargeInfo() {}

    public ChargeInfo(Integer beginCycleSeq) {
        this.beginCycleSeq = beginCycleSeq;
        this.chargeDetails = new LinkedList<>();
        this.totalAmount = 0;
        this.extraAmount = 0;
        this.parkingMinutes = 0;
        this.balancedMinutes = 0;
        this.discountedMinutes = 0;
    }

    public long beginTime() {
        return chargeDetails.peekFirst().beginTime;
    }

    public long endTime() {
        return chargeDetails.peekLast().endTime;
    }

    public int overlappedMinutes(long begin, long end) {
        long b = Long.max(begin, beginTime());
        long e = Long.min(end, endTime());
        int covered = b <= e ? (int)(e - b + 1) : 0;
        return Util.lengthToUnit(covered, Util.minuteSeconds);
    }

    @Override
    public String toString() {
        return "ChargeInfo{" + "beginCycleSeq=" + beginCycleSeq + ", cycleNumber=" + cycleNumber + ", chargeDetails=" +
               chargeDetails + ", parkingMinutes=" + parkingMinutes + ", balancedMinutes=" + balancedMinutes +
               ", discountedMinutes=" + discountedMinutes + ", totalAmount=" + totalAmount + ", extraAmount=" +
               extraAmount + '}';
    }
}