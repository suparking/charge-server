package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import net.suparking.chargeserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParamNotNull
public class ExtraAmountOverTimePoint extends FieldValidator {
    public Integer hour;
    public Integer minute;
    public Integer second;
    public Integer amount;

    private static final Logger log = LoggerFactory.getLogger(ExtraAmountOverTimePoint.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (Util.compareIntRangeClose(hour, 0, 23) != 0) {
            log.error("hour out of range");
            return false;
        }
        if (Util.compareIntRangeClose(minute, 0, 59) != 0) {
            log.error("minute out of range");
            return false;
        }
        if (Util.compareIntRangeClose(second, 0, 59) != 0) {
            log.error("second out of range");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExtraAmountOverTimePoint{" + "hour=" + hour + ", minute=" + minute + ", second=" + second +
               ", totalMaxAmount=" + amount + "} " + super.toString();
    }
}
