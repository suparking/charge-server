package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RangeRuleInfo extends FieldValidator {
    @ParamNotNull
    public RangeRuleType rangeRuleType;
    public Integer rangeFreeMinutes;
    public Boolean rangeFreeInCharge = false;
    public Integer unitLength;
    public Integer minUnitLength = 0;
    public Integer rangeMaxAmount;
    public Integer rangeMinAmount;
    public Integer amountPerUnit;
    public Integer fixedAmount;
    public List<AmountPerUnits> amountPerUnits;

    private static final Logger log = LoggerFactory.getLogger(RangeRuleInfo.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (rangeRuleType.equals(RangeRuleType.FIXED)) {
            if (fixedAmount == null) {
                log.error("rangeRuleType cannot be null");
                return false;
            }
        } else if (rangeRuleType.equals(RangeRuleType.UNIT)) {
            if (unitLength == null) {
                log.error("unitLength cannot be null");
                return false;
            } else if (amountPerUnit == null) {
                log.error("amountPerUnit cannot be null");
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "RangeRuleInfo{" + "rangeRuleType=" + rangeRuleType + ", rangeFreeMinutes=" + rangeFreeMinutes +
               ", rangeFreeInCharge=" + rangeFreeInCharge + ", unitLength=" + unitLength + ", minUnitLength=" +
               minUnitLength + ", rangeMaxAmount=" + rangeMaxAmount + ", rangeMinAmount=" + rangeMinAmount +
               ", amountPerUnit=" + amountPerUnit + ", fixedAmount=" + fixedAmount + ", amountPerUnits=" + amountPerUnits + '}';
    }
}