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

    @ParamNotNull
    public String projectNo;

    public static void reloadAll() {
        chargeCalenderDateRepository.reloadAll();;
    }

    public static void reload(ChargeCalender chargeCalender) {
        chargeCalenderDateRepository.reloadCalender(chargeCalender);
    }

    public static void unloadById(String projectNo, ObjectId id) {
        chargeCalenderDateRepository.unloadCalenderById(projectNo, id);
    }

    public static ObjectId getDateTypeIdByCalender(String projectNo, Calendar calendar) {
        return chargeCalenderDateRepository.getDateTypeIdByCalender(projectNo, calendar);
    }

    public static ObjectId getDateTypeByEpoch(String projectNo, long time) {
        return chargeCalenderDateRepository.getDateTypeIdByEpoch(projectNo, time);
    }

    public static ObjectId getDateTypeIdByYearMonthDay(String projectNo, int year, int month, int day) {
        return chargeCalenderDateRepository.getDateTypeIdByYearMonthDay(projectNo, year, month, day);
    }

    private static final ChargeCalenderDateRepository chargeCalenderDateRepository = ChargeServerApplication.getBean(
            "ChargeCalenderDateRepositoryImpl", ChargeCalenderDateRepository.class);


    @Override
    public String toString() {
        return "ChargeCalender{" + "id='" + id + '\'' + ", year=" + year + ", month=" + month + ", day=" + day +
               ", weekday=" + weekday + ", dateTypeId='" + dateTypeId + ", projectNo='" + projectNo + '\'' + '}';
    }
}
