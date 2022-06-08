package net.suparking.chargeserver.parking;

public enum EventType {
    EV_ENTER,
    EV_ENTER_SHARE,
    EV_PASS,
    EV_PASS_SHARE,
    EV_LEAVE,
    EV_SHARE_RETURN,
    EV_PREPAY,

    //以下创建订单时使用
    EV_ORDER_CREATE,
    EV_CAR_TYPE_CHANGE,
    EV_CHARGE_TYPE_CHANGE
}