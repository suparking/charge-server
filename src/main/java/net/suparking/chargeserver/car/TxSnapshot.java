package net.suparking.chargeserver.car;

public class TxSnapshot {
    public Integer value = 0;
    public Integer valueBefore = 0;
    public Integer valueAfter = 0;

    public TxSnapshot() {}
    
    public TxSnapshot(Integer valueBefore) {
        this.valueBefore = valueBefore;
    }

    public boolean effective() {
        return value > 0;
    }

    @Override
    public String toString() {
        return "TxSnapshot{" + "value=" + value + ", valueBefore=" + valueBefore + ", valueAfter=" + valueAfter + '}';
    }
}
