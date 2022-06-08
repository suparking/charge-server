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

@Document(collection = "online_wallet_account")
public class WalletAccount extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public List<String> userIds;
    public Integer balance;
    public Map<String, TxInfo> txInfoMap;
    @ParamNotNull
    public ObjectId carGroupId;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    private static WalletAccountRepository walletAccountRepository = ChargeServerApplication.getBean(
            "WalletAccountRepositoryImpl", WalletAccountRepository.class);
    private static WalletAccountProducer walletAccountProducer = ChargeServerApplication.getBean(
            "WalletAccountProducer", WalletAccountProducer.class);

    public static WalletAccount loadByCarGroup(CarGroup carGroup) {
        return walletAccountRepository.loadByCarGroup(carGroup);
    }

    public static WalletAccount loadByCarGroupId(ObjectId id) {
        return walletAccountRepository.loadByCarGroupId(id);
    }

    public static void removeByCarGroupId(ObjectId id) {
        List<WalletAccount> list = walletAccountRepository.removeByCarGroupId(id);
        if (Objects.nonNull(list) && list.size() > 0) {
            walletAccountProducer.publishWalletAccountRemove("wallet_account", list.stream().map(item -> item.id).collect(Collectors.toList()));
        }
    }

    public WalletAccount() {}

    public WalletAccount(CarGroup carGroup, Wallet wallet) {
        this.userIds = carGroup.userIds;
        this.balance = wallet.initialBalance;
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
            txInfoMap.put(userId + '-' + projectNo, new TxInfo(snapshot, expireTime));
        }

        snapshot.valueAfter = balance;
        save();
        return snapshot;
    }

    public synchronized TxSnapshot release(String key) {
        TxInfo reserved = txInfoMap.remove(key);
        if (reserved != null) {
            TxSnapshot snapshot = new TxSnapshot(balance);
            int value = reserved.txValue();
            balance += value;
            snapshot.value = value;
            snapshot.valueAfter = balance;
            save();
            return snapshot;
        }
        return null;
    }

    public synchronized void clear(String plateNo) {
        txInfoMap.remove(plateNo);
        save();
    }

    public boolean syncFromCarGroup(CarGroup carGroup) {
        boolean updated = false;
        if (plateNoChanged(carGroup.userIds)) {
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
        walletAccountRepository.save(this);
        walletAccountProducer.publishWalletAccountSave("online_wallet_account", this);
    }

    private boolean plateNoChanged(List<String> plateNos) {
        boolean updated = false;
        for (String plateNo: this.userIds) {
            if (!plateNos.contains(plateNo)) {
                txInfoMap.remove(plateNo);
                updated = true;
            }
        }
        for (String userId : userIds) {
            if (!this.userIds.contains(userId)) {
                updated = true;
            }
        }
        return updated;
    }

    @Override
    public String toString() {
        return "WalletAccount{" + "id=" + id + ", userIds=" + userIds + ", balance=" + balance + ", txInfoMap=" +
               txInfoMap + ", carGroupId=" + carGroupId + ", projectNo='" + projectNo + '\'' + ", creator='" + creator +
               '\'' + ", createTime=" + createTime + ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime +
               "} " + super.toString();
    }
}
