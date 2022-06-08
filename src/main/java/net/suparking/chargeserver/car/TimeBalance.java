package net.suparking.chargeserver.car;

import net.suparking.server.exception.FieldValidator;
import net.suparking.server.exception.ParamNotNull;

@ParamNotNull
public class TimeBalance extends FieldValidator {
    public Integer initialBalance;
    public BalancePolicy balancePolicy;

    public boolean dayRegular() {
        return balancePolicy.regularType.equals(RegularType.DAY);
    }

    public boolean monthRegular() {
        return balancePolicy.regularType.equals(RegularType.MONTH);
    }

    public boolean renewByAccumulate() {
        return balancePolicy.renewByAccumulate();
    }

    public boolean renewByReset() {
        return balancePolicy.renewByReset();
    }

    public Integer getPolicyValue() {
        return balancePolicy.value;
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return balancePolicy.validate();
    }

    @Override
    public String toString() {
        return "TimeBalance{" + "initialBalance=" + initialBalance + ", balancePolicy=" + balancePolicy + '}';
    }
}
