package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.util.Util;

class ChargeFrame {
    public Long beginTime;
    public Long endTime;
    public ChargeType chargeType;

    public ChargeFrame(long beginTime, long endTime, ChargeType chargeType) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.chargeType = chargeType;
    }

    public ChargeFrame(ChargeFrame chargeFrame) {
        this.beginTime = chargeFrame.beginTime;
        this.endTime = chargeFrame.endTime;
        this.chargeType = chargeFrame.chargeType;
    }

    public int calculateChargeMinute() {
        return Util.timeGapToMinute(beginTime, endTime);
    }
}
