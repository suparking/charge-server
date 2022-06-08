package net.suparking.chargeserver.charge;

public class TimeDeductInfo {
    public int discountedMinutes;
    public int balancedMinutes;

    public TimeDeductInfo(int discountedMinutes, int balancedMinutes) {
        this.discountedMinutes = discountedMinutes;
        this.balancedMinutes = balancedMinutes;
    }
}
