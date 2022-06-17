package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BalancePolicy extends FieldValidator {
    @ParamNotNull
    public RegularType regularType;
    public RenewType renewType;
    public Integer value;

    private static final Logger log = LoggerFactory.getLogger(BalancePolicy.class);

    public boolean renewByAccumulate() {
        return renewType != null && renewType.equals(RenewType.ACCUMULATE);
    }

    public boolean renewByReset() {
        return renewType != null && renewType.equals(RenewType.RESET);
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (Objects.isNull(regularType)) {
            log.error("regularType cannot be null");
            return false;
        }
        if (!regularType.equals(RegularType.NONE)) {
            if (value == null) {
                log.error("value cannot be null");
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "BalancePolicy{" + "regularType=" + regularType + ", renewType=" + renewType + ", value=" + value + '}';
    }
}
