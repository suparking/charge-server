package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;

import java.util.Collections;
import java.util.LinkedList;

@ParamNotNull
public class ComplicatedRule extends FieldValidator {
    public RangeType rangeType;
    public LinkedList<RangeRule> rangeRules;

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        for (RangeRule rangeRule: rangeRules) {
            if (!rangeRule.validate()) {
                return false;
            }
        }
        Collections.sort(rangeRules);
        return true;
    }

    @Override
    public String toString() {
        return "ComplicatedRule{" + "rangeType=" + rangeType + ", rangeRules=" + rangeRules + '}';
    }
}
