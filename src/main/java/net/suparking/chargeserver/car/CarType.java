package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.common.CarTypeClass;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "online_car_type")
public class CarType extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public CarTypeClass carTypeClass;
    @ParamNotNull
    public String carTypeName;
    @ParamNotNull
    public String carTypeAlias;

    @ParamNotNull
    public ObjectId defaultChargeTypeId;
    @ParamNotNull
    public List<ChargeStrategy> chargeStrategies;
    public MaxAmountForMultiParking maxAmountForMultiParking;
    public ExtraAmountOverTimePoint extraAmountOverTimePoint;
    public Boolean forceOpen = false;
    public Boolean barrierFree = false;
    public Boolean strictOnShared = false;
    public Boolean defaultType = false;
    public String remark;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static void reload(CarType carType) {
        carTypeRepository.reload(carType);
    }

    public static void unloadById(String projectNo, ObjectId id) {
        carTypeRepository.unloadById(projectNo, id);
    }

    public static CarType findById(String projectNo, ObjectId id) {
        return carTypeRepository.findById(projectNo, id);
    }

    public static boolean tempType(String projectNo, ObjectId id) {
        return carTypeRepository.tempType(projectNo, id);
    }

    private static CarTypeRepository carTypeRepository = ChargeServerApplication.getBean(
            "CarTypeRepositoryImpl", CarTypeRepository.class);

    private static final Logger log = LoggerFactory.getLogger(CarType.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }

        if (maxAmountForMultiParking != null) {
            if (!maxAmountForMultiParking.validate()) {
                return false;
            }
        }
        if (extraAmountOverTimePoint != null) {
            return extraAmountOverTimePoint.validate();
        }
        return true;
    }

    public ObjectId findChargeTypeId(Boolean multiParking, ObjectId subAreaId, ObjectId dateTypeId) {
        for (ChargeStrategy cs: chargeStrategies) {
            if (subAreaId.equals(cs.subAreaId)
                && dateTypeId.equals(cs.dateTypeId)) {
                if (cs.multiParking == null || multiParking.equals(cs.multiParking)) {
                    return cs.chargeTypeId;
                }
            }
        }
        return defaultChargeTypeId;
    }

    public MultiMaxPolicy multiMaxPolicy(long enterTime, long leaveTime) {
        return maxAmountForMultiParking != null
                ? maxAmountForMultiParking.multiMaxPolicy(enterTime, leaveTime) : null;
    }

    public ExtraPolicy extraPolicy() {
        if (extraAmountOverTimePoint != null) {
            ExtraPolicy extraPolicy = new ExtraPolicy();
            extraPolicy.amount = extraAmountOverTimePoint.amount;
            extraPolicy.time = Util.hmToSecond(extraAmountOverTimePoint.hour, extraAmountOverTimePoint.minute);
            extraPolicy.time += extraAmountOverTimePoint.second;
            return extraPolicy;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "CarType{" + "id=" + id + ", carTypeClass=" + carTypeClass + ", carTypeName='" + carTypeName + '\'' +
               ", carTypeAlias='" + carTypeAlias + '\'' + ", defaultChargeTypeId=" + defaultChargeTypeId +
                ", chargeStrategies=" + chargeStrategies + ", maxAmountForMultiParking=" + maxAmountForMultiParking + ", extraAmountOverTimePoint=" +
               extraAmountOverTimePoint + ", forceOpen=" + forceOpen + ", barrierFree=" + barrierFree +
               ", strictOnShared=" + strictOnShared + ", defaultType=" + defaultType + ", remark='" + remark + '\'' +
               ", projectNo='" + projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" + createTime +
               ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
