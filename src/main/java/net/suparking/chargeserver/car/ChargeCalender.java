package net.suparking.chargeserver.car;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;

@Document(collection = "online_charge_calender")
public class ChargeCalender extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public Integer year;
    @ParamNotNull
    public Integer month;
    @ParamNotNull
    public Integer day;
    public Integer weekday;
    @ParamNotNull
    public ObjectId dateTypeId;

    public static void reloadAll() {
        chargeCalenderDateRepository.reloadAll();;
    }

    public static void reload(ChargeCalender chargeCalender) {
        chargeCalenderDateRepository.reloadCalender(chargeCalender);
    }

    public static void unloadById(ObjectId id) {
        chargeCalenderDateRepository.unloadCalenderById(id);
    }

    public static ObjectId getDateTypeIdByCalender(Calendar calendar) {
        return chargeCalenderDateRepository.getDateTypeIdByCalender(calendar);
    }

    public static ObjectId getDateTypeByEpoch(long time) {
        return chargeCalenderDateRepository.getDateTypeIdByEpoch(time);
    }

    public static ObjectId getDateTypeIdByYearMonthDay(int year, int month, int day) {
        return chargeCalenderDateRepository.getDateTypeIdByYearMonthDay(year, month, day);
    }

    private static ChargeCalenderDateRepository chargeCalenderDateRepository = ChargeServerApplication.getBean(
            "ChargeCalenderDateRepositoryImpl", ChargeCalenderDateRepository.class);


    @Override
    public String toString() {
        return "ChargeCalender{" + "id='" + id + '\'' + ", year=" + year + ", month=" + month + ", day=" + day +
               ", weekday=" + weekday + ", dateTypeId='" + dateTypeId + '\'' + '}';
    }
}
