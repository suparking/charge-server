package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "online_protocol")
public class Protocol extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public ProtocolType protocolType;
    @ParamNotNull
    public String protocolName;
    public String carTypeName;
    @ParamNotNull
    public String carTypeId;
    // add expired car type Id;
    public String expiredCarTypeId;
    @ParamNotNull
    public ParkingPolicy parkingPolicy;
    public Integer maxFixedCarQuantity;
    public Boolean disableShareReturn = false;
    public MaxAmountForMultiParking maxAmountForMultiParking;
    public Duration duration;
    public Integer price;
    public List<UserService> userServices;
    public String protocolDesc;
    public String remark;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static void reload(Protocol protocol) {
        protocolRepository.reload(protocol);
    }

    public static void unloadById(ObjectId id) {
        protocolRepository.unloadById(id);
    }

    public static Protocol findById(ObjectId id) {
        return protocolRepository.findById(id);
    }

    public static List<Protocol> findAll() {
        return protocolRepository.findAll();
    }

    private static ProtocolRepository protocolRepository = ChargeServerApplication.getBean(
            "ProtocolRepositoryImpl", ProtocolRepository.class);

    private static final Logger log = LoggerFactory.getLogger(Protocol.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (!parkingPolicy.validate()) {
            return false;
        }
        if (maxAmountForMultiParking != null) {
            if (!maxAmountForMultiParking.validate()) {
                return false;
            }
        }
        if (protocolType.equals(ProtocolType.TIME_BASED)) {
            if (duration == null) {
                log.error("duration cannot be null");
                return false;
            }
            if (price == null) {
                log.error("price cannot be null");
                return false;
            }
        }
        return true;
    }

    public int spaceQuantityInSubArea(ObjectId subAreaId) {
        return parkingPolicy.spaceQuantityInSubArea(subAreaId);
    }

    public int totalSpaceQuantity() {
        return parkingPolicy.totalSpaceQuantity();
    }

    public MultiMaxPolicy multiMaxPolicy(long enterTime, long leaveTime) {
        return maxAmountForMultiParking != null
                ? maxAmountForMultiParking.multiMaxPolicy(enterTime, leaveTime) : null;
    }

    @Override
    public String toString() {
        return "Protocol{" + "id=" + id + ", protocolType=" + protocolType + ", protocolName='" + protocolName + '\'' +
               ", carTypeName='" + carTypeName + '\'' + ", carTypeId='" + carTypeId + '\'' + ", parkingPolicy=" +
               parkingPolicy + ", maxFixedCarQuantity=" + maxFixedCarQuantity + ", disableShareReturn=" +
               disableShareReturn + ", maxAmountForMultiParking=" + maxAmountForMultiParking + ", duration=" +
               duration + ", price=" + price + ", userServices=" + userServices + ", protocolDesc='" + protocolDesc + '\'' + ", remark='" + remark + '\'' +
               ", projectNo='" + projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" + createTime +
               ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + ", expiredCarTypeId=" + expiredCarTypeId +
                "} " + super.toString();
    }
}
