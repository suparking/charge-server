package net.suparking.chargeserver.parking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.suparking.chargeserver.car.CarGroupTraceInfo;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingTrigger {
    // 设备驶入ID
    public String recogId;

    //车辆驶入时间, 设备上报时间
    public Long recogTime;

    // 常升 降锁成功时间, 常降 升锁成功时间
    public Long openTime;

    public String deviceNo;

    // 车位ID
    public ObjectId parkId;

    // 车位编号
    public String parkNo;
    // 车位名称
    public String parkName;

    public ObjectId inSubAreaId;
    public String inSubAreaName;
    public ObjectId outSubAreaId;
    public String outSubAreaName;



    public ObjectId carTypeId;
    public String carTypeName;

    public CarGroupTraceInfo carGroupTraceInfo;
    public String operator;
    public String remark;
}
