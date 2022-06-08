package net.suparking.chargeserver.charge.repository;

import net.suparking.chargeserver.car.ChargeCalender;
import net.suparking.chargeserver.car.ChargeCalenderDateRepository;
import net.suparking.chargeserver.car.DateType;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Order(120)
@Repository("ChargeCalenderDateRepositoryImpl")
public class ChargeCalenderDateRepositoryImpl extends BasicRepositoryImpl
        implements ChargeCalenderDateRepository, CommandLineRunner {

    private final List<ChargeCalender> calenders = new LinkedList<>();
    private Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>> calenderMap = new HashMap<>();
    private final List<DateType> dateTypes = new LinkedList<>();
    private DateType defaultDateType;

    private static final Logger log = LoggerFactory.getLogger(ChargeCalenderDateRepositoryImpl.class);

    @Autowired
    public ChargeCalenderDateRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<DateType> dateTypes = template.findAll(DateType.class);
        for (DateType dt: dateTypes) {
            log.info(dt.toString());
            if (dt.validate()) {
                this.dateTypes.removeIf(dateType -> dateType.id.equals(dt.id));
                loadDateType(dt);
            } else {
                log.error("DateType " + dt.id.toString() + " validate failed");
            }
        }
        resetDefaultDateType();

        List<ChargeCalender> chargeCalenders = template.findAll(ChargeCalender.class);
        for (ChargeCalender cc : chargeCalenders) {
            reloadCalender(cc);
        }
    }

    @Override
    public synchronized void reloadCalender(ChargeCalender chargeCalender) {
        if (chargeCalender.validate()) {
            unloadCalenderById(chargeCalender.id);
            loadCalender(chargeCalender);
        } else {
            log.error("ChargeCalender " + chargeCalender.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadCalenderById(ObjectId id) {
        ChargeCalender cc = null;
        for (ChargeCalender chargeCalender: calenders) {
            if (chargeCalender.id.equals(id)) {
                cc = chargeCalender;
                break;
            }
        }
        if (cc != null) {
            calenders.remove(cc);
            Map<Integer, Map<Integer, ChargeCalender>> yearMap = calenderMap.get(cc.year);
            if (yearMap != null) {
                Map<Integer, ChargeCalender> monthMap = yearMap.get(cc.month);
                if (monthMap != null) {
                    monthMap.remove(cc.day);
                }
            }
        }
    }

    @Override
    public synchronized void reloadDateType(DateType dateType) {
        if (dateType.validate()) {
            dateTypes.removeIf(dt -> dt.id.equals(dateType.id));
            loadDateType(dateType);
            resetDefaultDateType();
        } else {
            log.error("DateType " + dateType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadDateTypeById(ObjectId id) {
        dateTypes.removeIf(dateType -> dateType.id.equals(id));
        resetDefaultDateType();
    }

    @Override
    public synchronized ObjectId getDateTypeIdByCalender(Calendar calendar) {
        return getDateTypeIdByYearMonthDay(calendar.get(Calendar.YEAR),
                                           calendar.get(Calendar.MONTH) + 1,
                                           calendar.get(Calendar.DATE));
    }

    @Override
    public synchronized ObjectId getDateTypeIdByEpoch(long time) {
        return getDateTypeIdByCalender(new Calendar.Builder().setInstant(time).build());
    }

    @Override
    public synchronized ObjectId getDateTypeIdByYearMonthDay(int year, int month, int day) {
        Map<Integer, Map<Integer, ChargeCalender>> yearMap = calenderMap.get(year);
        if (yearMap != null) {
            Map<Integer, ChargeCalender> monthMap = yearMap.get(month);
            if (monthMap != null) {
                ChargeCalender chargeCalender = monthMap.get(day);
                if (chargeCalender != null) {
                    return chargeCalender.dateTypeId;
                }
            }
        }
        return defaultDateType.id;
    }

    @Override
    public synchronized void run(String... args) {
        log.info("ChargeCalender init ...");
        reloadAll();
        for (Map.Entry<Integer, Map<Integer, Map<Integer, ChargeCalender>>> year: calenderMap.entrySet()) {
            for (Map.Entry<Integer, Map<Integer, ChargeCalender>> month: year.getValue().entrySet()) {
                for (Map.Entry<Integer, ChargeCalender> day: month.getValue().entrySet()) {
                    log.info(year.getKey().toString() + '-'
                             + month.getKey().toString() + '-'
                             + day.getKey().toString() + " >>> "
                             + day.getValue().dateTypeId);
                }
            }
        }
    }

    private void loadCalender(ChargeCalender chargeCalender) {
        calenderMap.putIfAbsent(chargeCalender.year, new HashMap<>());
        calenderMap.get(chargeCalender.year).putIfAbsent(chargeCalender.month, new HashMap<>());
        calenderMap.get(chargeCalender.year).get(chargeCalender.month).put(chargeCalender.day, chargeCalender);
        calenders.add(chargeCalender);
    }

    private void loadDateType(DateType dateType) {
        dateTypes.add(dateType);
    }

    private void resetDefaultDateType() {
        defaultDateType = null;
        for (DateType dt: dateTypes) {
            if (dt.defaultType) {
                defaultDateType = dt;
                break;
            }
        }
        if (defaultDateType == null) {
            defaultDateType = new DateType();
            defaultDateType.id = new ObjectId();
            defaultDateType.dateTypeName = "default";
            defaultDateType.defaultType = true;
        }
    }
}
