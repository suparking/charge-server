package net.suparking.chargeserver.car;

public class ExtraPolicy {
    public int amount;
    public int time;

    @Override
    public String toString() {
        return "ExtraPolicy{" + "totalMaxAmount=" + amount + ", time=" + time + '}';
    }
}
