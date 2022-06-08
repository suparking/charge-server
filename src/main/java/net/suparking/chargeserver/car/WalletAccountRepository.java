package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface WalletAccountRepository {
    WalletAccount loadByCarGroup(CarGroup carGroup);
    WalletAccount loadByCarGroupId(ObjectId id);
    void save(WalletAccount walletAccount);
    List<WalletAccount> removeByCarGroupId(ObjectId id);
}
