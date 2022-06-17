package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.Calendar;

public interface ChargeCalenderDateRepository {
    void reloadAll();
    void reloadCalender(ChargeCalender chargeCalender);
    void unloadCalenderById(String projectNo, ObjectId id);
    void reloadDateType(DateType dateType);
    void unloadDateTypeById(String projectNo, ObjectId id);
    ObjectId getDateTypeIdByCalender(String projectNo, Calendar calendar);
    ObjectId getDateTypeIdByEpoch(String projectNo, long time);
    ObjectId getDateTypeIdByYearMonthDay(String projectNo, int year, int month, int day);
}
