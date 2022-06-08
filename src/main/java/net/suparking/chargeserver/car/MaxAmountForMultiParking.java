package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.suparking.chargeserver.car.MultiParkingMaxType.DELTA;
import static net.suparking.chargeserver.car.MultiParkingRangeType.DAY_ENTER;
import static net.suparking.chargeserver.car.MultiParkingRangeType.DAY_LEAVE;
import static net.suparking.chargeserver.car.MultiParkingRangeType.DURATION_PAY;
import static net.suparking.chargeserver.car.MultiParkingRangeType.FROM_ENTER;
import static net.suparking.chargeserver.car.MultiParkingRangeType.FROM_LEAVE;

public class MaxAmountForMultiParking extends FieldValidator {
    public MultiParkingScopeType multiParkingScopeType;
    public MultiParkingMaxType multiParkingMaxType;
    @ParamNotNull
    public Integer maxAmount;
    @ParamNotNull
    public MultiParkingRangeType multiParkingRangeType;
    public Integer dayStartMinute;
    public Integer minutes;
    // 新增累加和计费规则
    public List<MultiParkingUnit> multiParkingUnits;

    private static final Logger log = LoggerFactory.getLogger(MaxAmountForMultiParking.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (dayRange()) {
            if (dayStartMinute == null) {
                dayStartMinute = 0;
            }
            if (Util.compareIntRangeOpen(dayStartMinute, -1, Util.dayMinutes) != 0) {
                log.error("dayStartMinute is invalid(" + dayStartMinute + ")");
                return false;
            }
        } else {
            if (minutes == null || minutes <= 0) {
                log.error("minutes is invalid(" + minutes + ")");
                return false;
            }
        }
        return true;
    }

    public MultiMaxPolicy multiMaxPolicy(long enterTime, long leaveTime) {
        MultiMaxPolicy policy = new MultiMaxPolicy(this);
        if (multiParkingRangeType.equals(FROM_ENTER)) {
            policy.begin = enterTime - minutes * Util.minuteSeconds;
            policy.end = leaveTime;
        } else if (multiParkingRangeType.equals(FROM_LEAVE) || multiParkingRangeType.equals(DURATION_PAY)) {
            long n = Util.lengthToUnit(Util.timeGapToMinute(enterTime, leaveTime), minutes);
            policy.begin = leaveTime - n * minutes * Util.minuteSeconds;
            policy.end = leaveTime;
        } else {
            policy.begin = Util.dayBeginTime(Util.shapeDayBegin(enterTime, dayStartMinute), dayStartMinute);
            policy.end = Util.dayEndTime(Util.shapeDayEnd(leaveTime, dayStartMinute), dayStartMinute);
        }
        return policy;
    }

    public boolean dayRange() {
        return DAY_ENTER.equals(multiParkingRangeType) || DAY_LEAVE.equals(multiParkingRangeType);
    }

    public boolean fromEnter() {
        return FROM_ENTER.equals(multiParkingRangeType);
    }

    public boolean durationPayRange() {
        return DURATION_PAY.equals(multiParkingRangeType);
    }

    public boolean deltaMax() {
        return DELTA.equals(multiParkingMaxType);
    }

    @Override
    public String toString() {
        return "MaxAmountForMultiParking{" + "multiParkingScopeType=" + multiParkingScopeType +
               ", multiParkingMaxType=" + multiParkingMaxType + ", maxAmount=" + maxAmount +
               ", multiParkingRangeType=" + multiParkingRangeType + ", dayStartMinute=" + dayStartMinute +
               ", minutes=" + minutes + ", multiParkingUnits=" + multiParkingUnits + "} " + super.toString();
    }
}
