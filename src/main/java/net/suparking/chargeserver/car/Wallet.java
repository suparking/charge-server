package net.suparking.chargeserver.car;

import net.suparking.server.exception.FieldValidator;
import net.suparking.server.exception.ParamNotNull;

import java.util.Objects;

public class Wallet extends FieldValidator {
    @ParamNotNull
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
    public Integer getrPolicyValue() {
        return balancePolicy.value;
    }

    @Override
    public boolean validate() {
       if (!super.validate()) {
           return false;
       }
       if (Objects.isNull(balancePolicy)) {
           return true;
       } else {
           return balancePolicy.validate();
       }
    }

    @Override
    public String toString() {
        return "Wallet{" + "initialBalance=" + initialBalance + ", balancePolicy=" + balancePolicy + '}';
    }
}
