package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;

@ParamNotNull
public class Duration extends FieldValidator {
    public DurationType durationType;
    public Integer quantity;

    @Override
    public String toString() {
        return "Duration{" + "durationType=" + durationType + ", quantity=" + quantity + '}';
    }
}
