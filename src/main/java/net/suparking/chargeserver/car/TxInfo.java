package net.suparking.chargeserver.car;

public class TxInfo {
    public TxSnapshot txSnapshot;
    public Long expireTime;

    public TxInfo() {}

    public TxInfo(TxSnapshot txSnapshot, long expireTime) {
        this.txSnapshot = txSnapshot;
        this.expireTime = expireTime;
    }

    public Integer txValue() {
        return txSnapshot.value;
    }
}
