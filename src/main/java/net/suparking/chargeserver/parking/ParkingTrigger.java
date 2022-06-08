package net.suparking.chargeserver.parking;

import lombok.Data;
import net.suparking.chargeserver.car.CarGroupTraceInfo;
import org.bson.types.ObjectId;

@Data
public class ParkingTrigger {
    // 设备驶入ID
    public String recogId;

    //车辆驶入时间, 设备上报时间
    public Long recogTime;

    // 常升 降锁成功时间, 常降 升锁成功时间
    public Long openTime;

    // 车位ID
    public ObjectId parkId;

    // 车位名称
    public String parkName;

    public ObjectId inSubAreaId;
    public String inSubAreaName;
    public ObjectId outSubAreaId;
    public String outSubAreaName;

    // 车位编号
    public String parkNo;

    public ObjectId carTypeId;
    public String carTypeName;

    public CarGroupTraceInfo carGroupTraceInfo;
    public String operator;
    public String remark;

    public ParkingTrigger() {}

}
