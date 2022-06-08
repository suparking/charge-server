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
    private TimeBalanceAccount timeBalanceAccount;
    private WalletAccount walletAccount;
    private Protocol protocol;
    private SpecialType specialType;

    private String projectNo;

    private static final Logger log = LoggerFactory.getLogger(CarContext.class);

    /**
     * TODO: 4 --> 查询用户停车上下文
     * @param userId
     * @return
     */
    public static CarContext findCarContext(String userId) {
        CarContext carContext = new CarContext();
        carContext.userId = userId;

        if (!userId.isEmpty()) {
            if (Whitelist.isInWhitelist(userId)) {
                carContext.specialType = WHITE;
            } else if (Blacklist.isInBlacklist(userId)) {
                carContext.specialType = BLACK;
            }

            CarGroup carGroup = CarGroup.findByUserId(userId);
            if (carGroup != null) {
                if ((carContext.protocol = Protocol.findById(carGroup.protocolId)) != null) {
                    carContext.carGroup = carGroup;
                    carContext.groupType = CarType.findById(carGroup.carTypeId);
                    if (carGroup.active()) {
                        carContext.carType = carContext.groupType;
                        carContext.timeBalanceAccount = TimeBalanceAccount.loadByCarGroup(carGroup);
                        carContext.walletAccount = WalletAccount.loadByCarGroup(carGroup);
                        return carContext;
                    } else {
                        // add carType change
                        if (Objects.nonNull(carContext.protocol.expiredCarTypeId)) {
                            carContext.carType = CarType.findById(new ObjectId(carContext.protocol.expiredCarTypeId));
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

    public boolean timeBalanceEnabled() {
        return timeBalanceAccount != null;
    }

    public boolean walletEnabled() {
        return walletAccount != null;
    }

    public Integer leftDay() {
        return active() ? carGroup.leftDay() : null;
    }

    public Integer spaceQuantity(ObjectId subAreaId) {
        return registered() ? protocol.spaceQuantityInSubArea(subAreaId) : null;
    }


    public TxSnapshot reserveTimeBalance(int value, long expireTime, String projectNo) {
        return timeBalanceEnabled() ? timeBalanceAccount.reserve(userId, value, expireTime, projectNo) : null;
    }

    public TxSnapshot reserveWallet(int value, long expireTime, String projectNo) {
        return walletEnabled() ? walletAccount.reserve(userId, value, expireTime, projectNo) : null;
    }

    public void clearTimeBalance() {
        if (timeBalanceAccount != null) {
            timeBalanceAccount.clear(userId);
        }
    }

    public void clearWallet() {
        if (walletAccount != null) {
            walletAccount.clear(userId);
        }
    }

    public void clear() {
        clearTimeBalance();
        clearWallet();
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

    public TimeBalanceAccount getTimeBalanceAccount() {
        return timeBalanceAccount;
    }

    public WalletAccount getWalletAccount() {
        return walletAccount;
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

    public void setTimeBalanceAccount(TimeBalanceAccount timeBalanceAccount) {
        this.timeBalanceAccount = timeBalanceAccount;
    }

    public void setWalletAccount(WalletAccount walletAccount) {
        this.walletAccount = walletAccount;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setSpecialType(SpecialType specialType) {
        this.specialType = specialType;
    }
}
