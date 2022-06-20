package net.suparking.chargeserver.car;

import net.suparking.chargeserver.common.CarTypeClass;
import net.suparking.chargeserver.common.SpecialType;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Objects;

import static net.suparking.chargeserver.common.SpecialType.BLACK;
import static net.suparking.chargeserver.common.SpecialType.CT_REVERSE;
import static net.suparking.chargeserver.common.SpecialType.HS_REVERSE_NO_PAY;
import static net.suparking.chargeserver.common.SpecialType.WHITE;

/**
 * TODO: 车辆一次记录的上下文
 */
public class CarContext {
    private String userId;
    private CarType carType;
    private CarGroup carGroup;
    private CarType groupType;
    private Protocol protocol;
    private SpecialType specialType;

    private String projectNo;

    private static final Logger log = LoggerFactory.getLogger(CarContext.class);

    /**
     * TODO: 4 --> 查询用户停车上下文
     * @param userId
     * @return
     */
    public static CarContext findCarContext(String projectNo, String userId) {
        CarContext carContext = new CarContext();
        carContext.userId = userId;

        if (!userId.isEmpty()) {
            CarGroup carGroup = CarGroup.findByUserId(projectNo, userId);
            if (carGroup != null) {
                if ((carContext.protocol = Protocol.findById(carGroup.protocolId)) != null) {
                    carContext.carGroup = carGroup;
                    carContext.groupType = CarType.findById(projectNo, carGroup.carTypeId);
                    if (carGroup.active()) {
                        carContext.carType = carContext.groupType;
                        return carContext;
                    } else {
                        // add carType change
                        if (Objects.nonNull(carContext.protocol.expiredCarTypeId)) {
                            carContext.carType = CarType.findById(projectNo, new ObjectId(carContext.protocol.expiredCarTypeId));
                            log.info(userId + " is out of date for protocol " + carGroup.protocolName +
                                    " carType is Changed" + carContext.carType.carTypeName);
                            return carContext;
                        } else {
                            log.info(userId + " is out of date for protocol " + carGroup.protocolName);
                        }
                    }
                } else {
                    log.warn(userId + " has no valid protocol for id " + carGroup.protocolId);
                }
            }
        }
        carContext.projectNo = projectNo;
        carContext.carType = CarType.findByProjectNoAndUserId(projectNo, userId);
        return carContext;
    }

    public CarContext() {}

    public boolean enableForceOpen() {
        return carType.forceOpen;
    }

    public boolean enableBarrierFree() {
        return carType.barrierFree;
    }

    public boolean enableStrictOnShared() {
        return carType.strictOnShared;
    }

    public boolean registered() {
        return carGroup != null;
    }

    public boolean active() {
        return registered() && carGroup.active();
    }

    public boolean timeBased() {
        return registered() && carGroup.timeBased();
    }

    public Integer leftDay() {
        return active() ? carGroup.leftDay() : null;
    }

    public Integer spaceQuantity(ObjectId subAreaId) {
        return registered() ? protocol.spaceQuantityInSubArea(subAreaId) : null;
    }

    //此处只是根据车辆类型的不计次数规则 得到 开始与结束时间,而不是拿到具体分段时间参数
    public MultiMaxPolicy multiMaxPolicy(long enterTime, long leaveTime) {
        MultiMaxPolicy policy = active() ? protocol.multiMaxPolicy(enterTime, leaveTime) : null;
        if (policy != null) {
            if (policy.deltaMax()) {
                int delta = carGroup.userIdCount() - protocol.totalSpaceQuantity();
                policy.multiplier = Integer.max(1, delta);
            }
        } else {
            policy = carType.multiMaxPolicy(enterTime, leaveTime);
        }

        if (policy != null) {
            if (policy.scopeForCarGroup() && active()) {
                policy.userIds = carGroup.userIds;
            } else {
                policy.userIds = new LinkedList<>();
                policy.userIds.add(userId);
            }
        }
        return policy;
    }

    public ExtraPolicy extraPolicy() {
        return carType.extraPolicy();
    }

    public boolean inWhiteList() {
        return specialType != null && specialType.equals(WHITE);
    }

    public boolean inReserveList() {
        return specialType != null && (specialType.equals(HS_REVERSE_NO_PAY) || specialType.equals(CT_REVERSE));
    }

    public boolean inBlackList() {
        return specialType != null && specialType.equals(BLACK);
    }

    //Getter&Setter

    public String getProjectNo() {return projectNo;}

    public String getUserId() {
        return userId;
    }

    public CarType getCarType() {
        return carType;
    }

    public ObjectId getCarTypeId() {
        return carType.id;
    }

    public CarTypeClass getCarTypeClass() {
        return carType.carTypeClass;
    }

    public String getCarTypeName() {
        return carType.carTypeName;
    }

    public String getCarTypeAlias() {
        return carType.carTypeAlias;
    }

    public CarGroup getCarGroup() {
        return carGroup;
    }

    public ObjectId getCarGroupId() {
        return carGroup != null ? carGroup.id : null;
    }

    public CarType getGroupType() {
        return groupType;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public SpecialType getSpecialType() {
        return specialType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }
    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public void setCarGroup(CarGroup carGroup) {
        this.carGroup = carGroup;
    }

    public void setGroupType(CarType groupType) {
        this.groupType = groupType;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setSpecialType(SpecialType specialType) {
        this.specialType = specialType;
    }
}
