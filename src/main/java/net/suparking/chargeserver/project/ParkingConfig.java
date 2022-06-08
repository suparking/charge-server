package net.suparking.chargeserver.project;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;

import java.util.Objects;

public class ParkingConfig extends FieldValidator {

    @ParamNotNull
    public Integer txTTL;
    @ParamNotNull
    public Integer minIntervalForDupRecog;
    public Integer minIntervalForDupPark;
    @ParamNotNull
    public Integer minParkingSecond;
    @ParamNotNull
    public Integer prepayFreeMinutes;
    // TODO add bind discount free time
    public Integer bindDiscountFreeMinutes;

    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return true;
    }

    public boolean enableMinIntervalForDupPark() {
       return Objects.nonNull(minIntervalForDupPark)  && minIntervalForDupPark > 0;
    }
    public boolean enableMinIntervalForDupRecog() {
        return Objects.nonNull(minIntervalForDupRecog) && minIntervalForDupRecog > 0;
    }

    public boolean enalbeMinParkingSecond() {
        return Objects.nonNull(minParkingSecond) && minParkingSecond > 0;
    }

    @Override
    public String toString() {
        return "ParkingConfig{" + "txTTL=" + txTTL + ", minIntervalForDupRecog=" +
               minIntervalForDupRecog + ", minIntervalForDupPark=" + minIntervalForDupPark + ", minParkingSecond=" + minParkingSecond + ", prepayFreeMinutes=" +
               prepayFreeMinutes + ", bindDiscountFreeMinutes=" + bindDiscountFreeMinutes + '}';
    }
}
