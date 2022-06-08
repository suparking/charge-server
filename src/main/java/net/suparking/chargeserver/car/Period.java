package net.suparking.chargeserver.car;

public class Period implements Comparable<Period> {
    public Long beginDate;
    public Long endDate;

    @Override
    public int compareTo(Period period) {
        return (int)(beginDate - period.beginDate);
    }

    @Override
    public String toString() {
        return "Period{" + "beginDate=" + beginDate + ", endDate=" + endDate + '}';
    }
}
