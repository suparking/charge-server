package net.suparking.chargeserver.car;

public enum MultiParkingRangeType {
    // 入场时所在自然日
    DAY_ENTER,
    // 出场时所在自然日
    DAY_LEAVE,
    // 从入场往后
    FROM_ENTER,
    // 从出场往前
    FROM_LEAVE,
    // 从出场往前(只包含缴费时间)
    DURATION_PAY
}
