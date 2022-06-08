package net.suparking.chargeserver.charge;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;

@ParamNotNull
public class RangeRule extends FieldValidator implements Comparable<RangeRule> {
    public Integer begin;
    public Integer end;
    public RangeRuleInfo rangeRuleInfo;

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return rangeRuleInfo.validate();
    }

    @Override
    public int compareTo(RangeRule rangeRule) {
        return begin - rangeRule.begin;
    }

    public boolean contains(int offset) {
        if (begin < end) {
            return offset >= begin && offset < end;
        } else {
            return (offset >= begin && offset < 1440) || (offset >= 0 && offset < end);
        }
    }

    public int rangeMinutes(int offset) {
        if (begin < end) {
            if (offset >= begin && offset < end) {
                return end - offset;
            } else {
                return 0;
            }
        } else {
            if (offset >= begin && offset < 1440) {
                return 1440 - offset + end;
            } else if (offset >= 0 && offset < end) {
                return end - offset;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "RangeRule{" + "begin=" + begin + ", end=" + end + ", rangeRuleInfo=" + rangeRuleInfo + "} " +
               super.toString();
    }
}
