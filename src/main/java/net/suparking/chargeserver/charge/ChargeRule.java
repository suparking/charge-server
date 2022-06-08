package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChargeRule extends FieldValidator {
    @ParamNotNull
    public RuleType ruleType = RuleType.FREE;
    public Integer minHourLength = 0;
    public Integer amountPerHour;
    public Integer amountPerTime;
    public CycleType cycleType;
    public Integer dayStartMinute;
    public Integer cycleLength;
    public Integer cycleFreeMinutes;
    public Boolean cycleFreeInCharge;
    public Integer cycleMaxAmount;
    public Integer cycleMinAmount;
    public ComplicatedRule complicatedRule;

    private static final Logger log = LoggerFactory.getLogger(ChargeRule.class);

    public ChargeRule() {}

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (!ruleType.equals(RuleType.FREE)) {
            if (cycleType == null) {
                log.error("cycleType cannot be null");
                return false;
            } else if (cycleType.equals(CycleType.NATURAL)) {
                if (dayStartMinute == null) {
                    dayStartMinute = 0;
                }
                if (Util.compareIntRangeOpen(dayStartMinute, -1 ,Util.dayMinutes) != 0) {
                    log.error("dayStartMinute is invalid(" + dayStartMinute + ")");
                    return false;
                }
            } else if (cycleLength == null || cycleLength <= 0) {
                log.error("cycleLength is invalid(" + cycleType + ")");
                return false;
            }

            if (ruleType.equals(RuleType.HOUR)) {
                if (amountPerHour == null) {
                    log.error("amountPerHour cannot be null");
                    return false;
                }
            } else if (ruleType.equals(RuleType.TIME)) {
                if (amountPerTime == null) {
                    log.error("amountPerTime cannot be null");
                    return false;
                }
            } else if (ruleType.equals(RuleType.COMPLICATED)) {
                if (complicatedRule == null) {
                    log.error("complicatedRule cannot be null");
                    return false;
                } else {
                    return complicatedRule.validate();
                }
            }
        }
        return true;
    }

    public boolean chargeForFree() {
        return ruleType.equals(RuleType.FREE);
    }

    public boolean validForMerge(ChargeRule chargeRule) {
        if (!chargeForFree() && !chargeRule.chargeForFree()) {
            if (!cycleType.equals(chargeRule.cycleType)) {
                return false;
            } else if (cycleType.equals(CycleType.ENTER)) {
                if (!cycleLength.equals(chargeRule.cycleLength)) {
                    return false;
                }
            } else if (cycleType.equals(CycleType.NATURAL)) {
                if (dayStartMinute != chargeRule.dayStartMinute) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChargeRule{" + "ruleType=" + ruleType + ", minHourLength=" + minHourLength + ", amountPerHour=" +
               amountPerHour + ", amountPerTime=" + amountPerTime + ", cycleType=" + cycleType + ", dayStartMinute=" +
               dayStartMinute + ", cycleLength=" + cycleLength + ", cycleFreeMinutes=" + cycleFreeMinutes +
               ", cycleFreeInCharge=" + cycleFreeInCharge + ", cycleMaxAmount=" + cycleMaxAmount + ", cycleMinAmount=" +
               cycleMinAmount + ", complicatedRule=" + complicatedRule + '}';
    }
}