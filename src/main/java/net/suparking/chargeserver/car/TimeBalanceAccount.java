package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.suparking.chargeserver.car.TimeBalanceAccountLogType.RELEASE;
import static net.suparking.chargeserver.car.TimeBalanceAccountLogType.RESERVE;

@Document(collection = "online_time_balance_account")
public class TimeBalanceAccount extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public List<String> userIds;
    @ParamNotNull
    public Integer balance;
    public Map<String, TxInfo> txInfoMap;
    @ParamNotNull
    public ObjectId carGroupId;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    private static TimeBalanceAccountRepository timeBalanceAccountRepository = ChargeServerApplication.getBean(
            "TimeBalanceAccountRepositoryImpl", TimeBalanceAccountRepository.class);
    private static TimeBalanceAccountProducer timeBalanceAccountProducer = ChargeServerApplication.getBean(
            "TimeBalanceAccountProducer", TimeBalanceAccountProducer.class);

    public static TimeBalanceAccount loadByCarGroup(CarGroup carGroup) {
        return timeBalanceAccountRepository.loadByCarGroup(carGroup);
    }

    public static TimeBalanceAccount loadByCarGroupId(ObjectId id) {
        return timeBalanceAccountRepository.loadByCarGroupId(id);
    }

    public static void removeByCarGroupId(ObjectId id) {
        List<TimeBalanceAccount> list = timeBalanceAccountRepository.removeByCarGroupId(id);
        if (Objects.nonNull(list) && list.size() > 0) {
            timeBalanceAccountProducer.publishTimeBalanceAccountRemove("online_time_balance_account", list.stream().map(item -> item.id).collect(Collectors.toList()));
        }
    }

    public TimeBalanceAccount() {}

    public TimeBalanceAccount(CarGroup carGroup, TimeBalance timeBalance) {
        this.userIds = carGroup.userIds;
        this.balance = timeBalance.initialBalance;
        this.txInfoMap = new HashMap<>();
        this.carGroupId = carGroup.id;
        this.projectNo = carGroup.getProjectNo();
        this.creator = "system";
        this.createTime = Util.currentEpoch();
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (txInfoMap == null) {
            txInfoMap = new HashMap<>();
        }
        return true;
    }

    public synchronized TxSnapshot inc(int value) {
        TxSnapshot snapshot = new TxSnapshot(balance);
        balance += value;
        snapshot.value = value;
        snapshot.valueAfter = balance;
        save();
        return snapshot;
    }

    public synchronized TxSnapshot dec(int value) {
        TxSnapshot snapshot = new TxSnapshot(balance);
        int decValue = Integer.min(value, balance);
        balance -= decValue;
        snapshot.value = decValue;
        snapshot.valueAfter = balance;
        save();
        return snapshot;
    }

    public synchronized TxSnapshot set(int value) {
        TxSnapshot snapshot = new TxSnapshot(balance);
        balance = value;
        snapshot.value = value;
        snapshot.valueAfter = balance;
        save();
        return snapshot;
    }

    public synchronized TxSnapshot reset(int value) {
        txInfoMap.clear();
        return set(value);
    }

    public synchronized TxSnapshot reserve(String userId, int value, long expireTime, String projectNo) {
        release(userId + '-' + projectNo);
        for (String pn: txInfoMap.keySet()) {
            if (txInfoMap.get(pn).expireTime < Util.currentEpoch()) {
                release(pn);
            }
        }

        TxSnapshot snapshot = new TxSnapshot(balance);
        if (balance > 0 && value > 0) {
            if (value < balance) {
                snapshot.value = value;
                balance -= value;
            } else {
                snapshot.value = balance;
                balance = 0;
            }
            snapshot.valueAfter = balance;
            txInfoMap.put(projectNo + '-' + userId, new TxInfo(snapshot, expireTime));
            new TimeBalanceAccountLog(RESERVE, snapshot, userId, carGroupId, projectNo).save();
        }

        snapshot.valueAfter = balance;
        save();
        return snapshot;
    }

    public synchronized void clear(String plateNo) {
        txInfoMap.remove(plateNo);
        save();
    }

    public boolean syncFromCarGroup(CarGroup carGroup) {
        boolean updated = false;
        if (userIdChanged(carGroup.userIds)) {
            userIds = carGroup.userIds;
            modifier = "system";
            modifyTime = Util.currentEpoch();
            updated = true;
        }
        return updated;
    }

    private void save() {
        modifier = "system";
        modifyTime = Util.currentEpoch();
        timeBalanceAccountRepository.save(this);
        timeBalanceAccountProducer.publishTimeBalanceAccountSave("time_balance_account", this);
    }

    /**
     * key = userId + '-' + projectNo
     * @param key
     * @return
     */
    private synchronized TxSnapshot release(String key) {
        TxInfo reserved = txInfoMap.remove(key);
        if (reserved != null) {
            TxSnapshot snapshot = new TxSnapshot(balance);
            Integer value = reserved.txValue();
            balance += value;
            snapshot.value = value;
            snapshot.valueAfter = balance;
            new TimeBalanceAccountLog(RELEASE, snapshot, key, carGroupId, projectNo).save();
            return snapshot;
        }
        return null;
    }

    private boolean userIdChanged(List<String> userIds) {
        boolean updated = false;
        for (String userId: this.userIds) {
            if (!userIds.contains(userId)) {
                txInfoMap.remove(userId);
                updated = true;
            }
        }
        for (String userId: userIds) {
            if (!this.userIds.contains(userId)) {
                return true;
            }
        }
        return updated;
    }

    @Override
    public String toString() {
        return "TimeBalanceAccount{" + "id=" + id + ", userIds=" + userIds + ", balance=" + balance + ", txInfoMap=" +
               txInfoMap + ", carGroupId=" + carGroupId + ", projectNo='" + projectNo + '\'' + ", creator='" + creator +
               '\'' + ", createTime=" + createTime + ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime +
               "} " + super.toString();
    }
}
