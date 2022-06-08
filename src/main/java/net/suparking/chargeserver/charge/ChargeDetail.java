package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.util.Util;

public class ChargeDetail {
    public String chargeTypeName;
    public Long beginTime;
    public Long endTime;
    public Integer parkingMinutes;
    public Integer balancedMinutes;
    public Integer freedMinutes;
    public Integer discountedMinutes;
    public Integer chargingMinutes;
    public Integer chargeAmount = 0;
    public String remark;

    public ChargeDetail() {}

    public ChargeDetail(long beginTime, long endTime, int balancedMinutes,
                        int freedMinutes, int discountedMinutes, String chargeTypeName) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.parkingMinutes = Util.timeGapToMinute(beginTime, endTime);
        this.balancedMinutes = balancedMinutes;
        this.freedMinutes = freedMinutes;
        this.discountedMinutes = discountedMinutes;
        this.chargingMinutes = this.parkingMinutes - freedMinutes - balancedMinutes - discountedMinutes;
        if (this.chargingMinutes < 0) this.chargingMinutes = 0;
        this.chargeTypeName = chargeTypeName;
    }

    @Override
    public String toString() {
        return "ChargeDetail{" + "chargeTypeName='" + chargeTypeName + '\'' + ", begin=" + beginTime +
               ", end=" + endTime + ", parkingMinutes=" + parkingMinutes + ", balancedMinutes=" + balancedMinutes +
               ", freedMinutes=" + freedMinutes + ", discountedMinutes=" + discountedMinutes + ", chargingMinutes=" +
               chargingMinutes + ", chargeAmount=" + chargeAmount + ", remark='" + remark + '\'' + '}';
    }
}