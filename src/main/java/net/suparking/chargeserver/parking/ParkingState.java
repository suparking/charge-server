package net.suparking.chargeserver.parking;

public enum ParkingState {
    //以下均为完成开闸状态
    ENTERED,            //已入场
    ENTERED_FORCE,      //已入场（强制开闸）1通道开启强制开闸模式2白名单起作用
    LEFT,               //已出场
    LEFT_FORCE,         //已出场（强制开闸）1通道开启强制开闸模式2白名单起作用3车辆类型免匹配
    LEFT_ABNORMAL,      //已出场（异常开闸）

    //以下均为等待开闸状态
    ENTER_WAIT,         //等待入场
    PAY_WAIT,           //等待支付
    LEAVE_UNMATCHED,    //等待出场（未匹配）
    PASS_UNMATCHED,     //等待内部入场（未匹配）
}
