package net.suparking.chargeserver.car;

import java.util.List;

public class MultiMaxPolicy {
   // TODO 增加多用户共存
    public List<String> userIds;
    public long begin;
    public long end;
    public int multiplier = 1;
    public MaxAmountForMultiParking multiParking;

    public MultiMaxPolicy(MaxAmountForMultiParking multiParking) {
        this.multiParking = multiParking;
    }

    public boolean scopeForCarGroup() {
        return multiParking.multiParkingScopeType != null
               && multiParking.multiParkingScopeType.equals(MultiParkingScopeType.CAR_GROUP);
    }

    public boolean dayRange() {
        return multiParking.dayRange();
    }

    public boolean fromEnter() {
        return multiParking.fromEnter();
    }

    public boolean durationPayRange() {
        return multiParking.durationPayRange();
    }

    public boolean deltaMax() {
        return multiParking.deltaMax();
    }

    public Integer getMaxAmount() {
        return multiParking.maxAmount * multiplier;
    }
    
    public Integer getMinutes() {
        return multiParking.minutes;
    }

    @Override
    public String toString() {
        return "MultiMaxPolicy{" + "userIds=" + userIds + ", begin=" + begin + ", end=" + end +
               ", multiParking=" + multiParking + '}';
    }
}
