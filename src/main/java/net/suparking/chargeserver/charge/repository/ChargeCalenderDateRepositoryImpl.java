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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Order(120)
@Repository("ChargeCalenderDateRepositoryImpl")
public class ChargeCalenderDateRepositoryImpl extends BasicRepositoryImpl
        implements ChargeCalenderDateRepository, CommandLineRunner {

    private final Map<String, List<ChargeCalender>> calendersMap = new ConcurrentHashMap<>(10);

    private final Map<String, Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>>> calenderMapMap = new ConcurrentHashMap<>(10);

    private final Map<String, List<DateType>> dateTypesMap = new ConcurrentHashMap<>(10);

    private final Map<String,DateType> defaultDateTypeMap = new ConcurrentHashMap<>(10);

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
                List<DateType> tDateTypes = getDateTypes(dt.projectNo);
                if (Objects.nonNull(tDateTypes)) {
                    tDateTypes.removeIf(dateType -> dateType.id.equals(dt.id));
                    dateTypesMap.put(dt.projectNo, tDateTypes);
                }
                loadDateType(dt);
                resetDefaultDateType(dt.projectNo);
            } else {
                log.error("DateType " + dt.id.toString() + " validate failed");
            }
        }


        List<ChargeCalender> chargeCalenders = template.findAll(ChargeCalender.class);
        for (ChargeCalender cc : chargeCalenders) {
            reloadCalender(cc);
        }
    }

    @Override
    public synchronized void reloadCalender(ChargeCalender chargeCalender) {
        if (chargeCalender.validate()) {
            unloadCalenderById(chargeCalender.projectNo, chargeCalender.id);
            loadCalender(chargeCalender.projectNo, chargeCalender);
        } else {
            log.error("ChargeCalender " + chargeCalender.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadCalenderById(String projectNo, ObjectId id) {
        ChargeCalender cc = null;
        List<ChargeCalender> calenders = getCalenderList(projectNo);
        if (Objects.nonNull(calenders)) {
            for (ChargeCalender chargeCalender: calenders) {
                if (chargeCalender.id.equals(id)) {
                    cc = chargeCalender;
                    break;
                }
            }
        }

        if (cc != null) {
            calenders.remove(cc);
            Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>> calenderMap = calenderMapMap.get(projectNo);
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
            List<DateType> dateTypes = getDateTypes(dateType.projectNo);
            if (Objects.nonNull(dateTypes)) {
                dateTypes.removeIf(dt -> dt.id.equals(dateType.id));
            }
            loadDateType(dateType);
            resetDefaultDateType(dateType.projectNo);
        } else {
            log.error("DateType " + dateType.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadDateTypeById(String projectNo, ObjectId id) {
        List<DateType> dateTypes = getDateTypes(projectNo);
        if (Objects.nonNull(dateTypes)) {
            dateTypes.removeIf(dateType -> dateType.id.equals(id));
            dateTypesMap.put(projectNo, dateTypes);
        }
        resetDefaultDateType(projectNo);
    }

    @Override
    public synchronized ObjectId getDateTypeIdByCalender(String projectNo, Calendar calendar) {
        return getDateTypeIdByYearMonthDay(projectNo, calendar.get(Calendar.YEAR),
                                           calendar.get(Calendar.MONTH) + 1,
                                           calendar.get(Calendar.DATE));
    }

    @Override
    public synchronized ObjectId getDateTypeIdByEpoch(String projectNo, long time) {
        return getDateTypeIdByCalender(projectNo, new Calendar.Builder().setInstant(time).build());
    }

    @Override
    public synchronized ObjectId getDateTypeIdByYearMonthDay(String projectNo, int year, int month, int day) {
        Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>> calenderMap = getCalenderMap(projectNo);
        if (Objects.nonNull(calenderMap)) {
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
        }
        return defaultDateTypeMap.get(projectNo).id;
    }

    @Override
    public void reloadByProjectNo(String projectNo) {
        List<DateType> dateTypes = findDateTypeByProjectNo(projectNo);
        for (DateType dt: dateTypes) {
            log.info(dt.toString());
            if (dt.validate()) {
                List<DateType> tDateTypes = getDateTypes(dt.projectNo);
                if (Objects.nonNull(tDateTypes)) {
                    tDateTypes.removeIf(dateType -> dateType.id.equals(dt.id));
                    dateTypesMap.put(dt.projectNo, tDateTypes);
                }
                loadDateType(dt);
                resetDefaultDateType(dt.projectNo);
            } else {
                log.error("DateType " + dt.id.toString() + " validate failed");
            }
        }


        List<ChargeCalender> chargeCalenders = findChargeCalenderByProjectNo(projectNo);
        for (ChargeCalender cc : chargeCalenders) {
            reloadCalender(cc);
        }
    }

    private List<ChargeCalender> findChargeCalenderByProjectNo(String projectNo) {
        Query query = new Query(Criteria.where("projectNo").is(projectNo));
        return template.find(query, ChargeCalender.class);
    }

    private List<DateType> findDateTypeByProjectNo(String projectNo) {
        Query query = new Query(Criteria.where("projectNo").is(projectNo));
        return template.find(query, DateType.class);
    }

    @Override
    public synchronized void run(String... args) {
        log.info("ChargeCalender init ...");
        reloadAll();
        for (Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>>> calenderMapNode : calenderMapMap.entrySet()) {
            for (Map.Entry<Integer, Map<Integer, Map<Integer, ChargeCalender>>> year: calenderMapNode.getValue().entrySet()) {
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

    }

    private Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>> getCalenderMap(String projectNo) {
        return calenderMapMap.get(projectNo);
    }

    private List<DateType> getDateTypes(String projectNo) {
        return dateTypesMap.get(projectNo);
    }

    private List<ChargeCalender> getCalenderList(String projectNo) {
        return calendersMap.get(projectNo);
    }

    private void loadCalender(String projectNo, ChargeCalender chargeCalender) {
        Map<Integer, Map<Integer, Map<Integer, ChargeCalender>>> calenderMap = calenderMapMap.get(projectNo);
        if (Objects.nonNull(calenderMap)) {
            calenderMap.putIfAbsent(chargeCalender.year, new HashMap<>());
            calenderMap.get(chargeCalender.year).putIfAbsent(chargeCalender.month, new HashMap<>());
            calenderMap.get(chargeCalender.year).get(chargeCalender.month).put(chargeCalender.day, chargeCalender);
            calenderMapMap.put(projectNo, calenderMap);
        }
        calenderMap = new HashMap<>(1);
        if (calenderMapMap.containsKey(projectNo)) {
            calenderMap.putIfAbsent(chargeCalender.year, new HashMap<>());
            calenderMap.get(chargeCalender.year).putIfAbsent(chargeCalender.month, new HashMap<>());
            calenderMap.get(chargeCalender.year).get(chargeCalender.month).put(chargeCalender.day, chargeCalender);
            calenderMapMap.put(projectNo, calenderMap);
            if (calendersMap.containsKey(projectNo)) {
                calendersMap.get(projectNo).add(chargeCalender);
            } else {
                List<ChargeCalender> chargeCalenderList = new ArrayList<>(1);
                chargeCalenderList.add(chargeCalender);
                calendersMap.put(projectNo, chargeCalenderList);
            }
        } else {
            calenderMap.putIfAbsent(chargeCalender.year, new HashMap<>());
            calenderMap.get(chargeCalender.year).putIfAbsent(chargeCalender.month, new HashMap<>());
            calenderMap.get(chargeCalender.year).get(chargeCalender.month).put(chargeCalender.day, chargeCalender);
            calenderMapMap.put(projectNo, calenderMap);

            List<ChargeCalender> calenders = new LinkedList<>();
            calenders.add(chargeCalender);
            calendersMap.put(projectNo, calenders);
        }
    }

    private void loadDateType(DateType dateType) {
        if (dateTypesMap.containsKey(dateType.projectNo)) {
            dateTypesMap.get(dateType.projectNo).add(dateType);
        } else {
            List<DateType> dateTypes = new LinkedList<>();
            dateTypes.add(dateType);
            dateTypesMap.put(dateType.projectNo, dateTypes);
        }
    }

    private void resetDefaultDateType(String projectNo) {
        DateType defaultDateType = null;
        List<DateType> dateTypes = getDateTypes(projectNo);
        if (Objects.nonNull(dateTypes)) {
            for (DateType dt: dateTypes) {
                if (dt.defaultType) {
                    defaultDateType = dt;
                    break;
                }
            }
        }

        if (defaultDateType == null) {
            defaultDateType = new DateType();
            defaultDateType.id = new ObjectId();
            defaultDateType.dateTypeName = "default";
            defaultDateType.defaultType = true;
            defaultDateType.projectNo = projectNo;
        }
        defaultDateTypeMap.put(projectNo, defaultDateType);
    }
}
