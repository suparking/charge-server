package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "car")
public class User {
    @Id
    public ObjectId id;
    @ParamNotNull
    public String userId;
    @ParamNotNull
    public String plateType;
    public String vehicleType;
    public String brand;
    public String vehicleLicenseURL;
    public UserDriver userDriver;
    public ObjectId carGroupId;
    public String carTypeName;
    public ObjectId carTypeId;
    public String protocolName;
    public ObjectId protocolId;
    @ParamNotNull
    public UserState userState;
    public String remark;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", userId='" + userId + '\'' + ", plateType='" + plateType + '\'' +
               ", vehicleType='" + vehicleType + '\'' + ", brand='" + brand + '\'' + ", vehicleLicenseURL='" +
               vehicleLicenseURL + '\'' + ", userDriver=" + userDriver + ", carGroupId=" + carGroupId +
               ", carTypeName='" + carTypeName + '\'' + ", carTypeId=" + carTypeId + ", protocolName='" + protocolName +
               '\'' + ", protocolId=" + protocolId + ", userState=" + userState + ", remark='" + remark + '\'' +
               ", projectNo='" + projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" + createTime +
               ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
