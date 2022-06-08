package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.Calendar;

public interface ChargeCalenderDateRepository {
    void reloadAll();
    void reloadCalender(ChargeCalender chargeCalender);
    void unloadCalenderById(ObjectId id);
    void reloadDateType(DateType dateType);
    void unloadDateTypeById(ObjectId id);
    ObjectId getDateTypeIdByCalender(Calendar calendar);
    ObjectId getDateTypeIdByEpoch(long time);
    ObjectId getDateTypeIdByYearMonthDay(int year, int month, int day);
}
