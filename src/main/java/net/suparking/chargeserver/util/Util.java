package net.suparking.chargeserver.util;

import cn.hutool.core.date.format.FastDateFormat;
import net.suparking.chargeserver.ApplicationProperties;
import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.car.MultiParkingUnit;
import net.suparking.chargeserver.charge.AmountPerUnits;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {
    public static final Integer MAGIC_NUMBER = 1;
    public static final Integer MAGIC_ZERO = 0;

    public static final int dayHours = 24;
    public static final int dayMinutes = 1440;
    public static final int daySeconds = 86400;
    public static final int hourMinutes = 60;
    public static final int hourSeconds = 3600;
    public static final int minuteSeconds = 60;

    public static final int minH = 0;
    public static final int maxH = 23;
    public static final int minM = 0;
    public static final int maxM = 59;
    public static final int minS = 0;
    public static final int maxS = 59;

    public static Date now;
    private static AtomicInteger orderSeq;
    private static AtomicInteger paySeq;
    public static SimpleDateFormat timestampFormat;
    public static SimpleDateFormat shortTimeStampFormat;
    public static SimpleDateFormat ymdFormat;
    public static SimpleDateFormat simpleYmdFormat;
    public static SimpleDateFormat hmsFormat;
    public static SimpleDateFormat ymdFormatCh;
    public static SimpleDateFormat simpleDateFormat;
    public static SimpleDateFormat etcTimeFormat;
    public static SimpleDateFormat simpleDateFormatCh;
    public static ApplicationProperties appProperties;

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    static {
        try {
            now = null;
            orderSeq = new AtomicInteger(0);
            paySeq = new AtomicInteger(0);
            timestampFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            shortTimeStampFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            ymdFormat = new SimpleDateFormat("yyyyMMdd");
            simpleYmdFormat = new SimpleDateFormat("yyyy-MM-dd");
            hmsFormat = new SimpleDateFormat("HHmmss");
            ymdFormatCh = new SimpleDateFormat("yyyy年MM月dd日");
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            etcTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormatCh = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
            appProperties = ChargeServerApplication.getBean("ApplicationProperties", ApplicationProperties.class);
        } catch (Exception e) {
            for (StackTraceElement ste: e.getStackTrace()) {
                log.error(ste.toString());
            }
        }
    }
    public static Double converAmount(Integer amount) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.valueOf(df.format(amount.floatValue() / 100));
    }

    public static String converHcDate(Long mill) {
       return simpleDateFormat.format(new Date(mill * 1000));
    }

    /**
     * ISO 8602
     * @param mill
     * @return
     */
    public static String converISO8602Date(Long mill) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ssZZ";
        FastDateFormat format = FastDateFormat.getInstance(pattern);
        return format.format(mill *  1000);
    }
    public static void doNothing() {}

    public static void setNow(Date now) {
        Util.now = now;
    }

    public static int timeGapToDay(long time1, long time2) {
        return time1 < time2 ? secondToDay(time2-time1) : secondToDay(time1-time2);
    }

    public static int secondToMinute(long s) {
        return (int)(s>0?(s-1)/minuteSeconds+1:0);
    }

    public static int secondToDay(long s) {
        return (int)(s>0?(s-1)/daySeconds+1:0);
    }

    public static int minuteToHour(int m) {
        return minuteToHour(m, 0);
    }

    public static int minuteToHour(int m, int minLen) {
        return m/hourMinutes+((m%hourMinutes>minLen)?1:0);
    }

    public static int lengthToUnit(int l, int u) {
        return lengthToUnit(l, u, 0);
    }

    public static int lengthToUnit(int l, int u, int minLen) {
        return l/u+((l%u>minLen)?1:0);
    }

    public static long lengthToUnit(long l, long u) {
        return lengthToUnit(l, u, 0);
    }

    public static long lengthToUnit(long l, long u, long minLen) {
        return l/u+((l%u>minLen)?1:0);
    }

    public static int hmToSecond(int hour, int minute) {
        return hour*hourSeconds+minute*minuteSeconds;
    }

    public static int timeGapToMinute(long time1, long time2) {
        return time1 < time2 ? secondToMinute(time2-time1) : secondToMinute(time1-time2);
    }
    public static int compareIntRangeOpen(int i, int begin, int end) {
        if (i <= begin) {
            return -1;
        } else if (i >= end) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int compareIntRangeClose(int i, int begin, int end) {
        if (i < begin) {
            return -1;
        } else if (i > end) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int compareLongRangeClose(long i, long begin, long end) {
        if (i < begin) {
            return -1;
        } else if (i > end) {
            return 1;
        } else {
            return 0;
        }
    }

    public static float RMBFenToYuan(int amount) {
        return (float) amount / 100;
    }

    public static Calendar dateToCalendar(long time) {
        return new Calendar.Builder().setInstant(time*1000).build();
    }

    public static long dayBeginTime(long time) {
        return dayBeginTime(time, 0);
    }

    public static long dayBeginTime(long time, int dayStartMinute) {
        Calendar calendar = new Calendar.Builder().setInstant(time*1000).build();
        long dayBegin = dayBeginTime(calendar, dayStartMinute).toInstant().getEpochSecond();
        return dayBegin <= time ? dayBegin : dayBegin - daySeconds;
    }

    private static Date dayBeginTime(Calendar calendar, int dayStartMinute) {
        int h = minH;
        int m = minM;
        if (compareIntRangeOpen(dayStartMinute, 0, dayMinutes) == 0) {
            h = dayStartMinute / hourMinutes;
            m = dayStartMinute % hourMinutes;
        }
        Calendar cln = (Calendar) calendar.clone();
        cln.set(Calendar.HOUR_OF_DAY, h);
        cln.set(Calendar.MINUTE, m);
        cln.set(Calendar.SECOND, minS);
        return cln.getTime();
    }

    public static long dayEndTime(long time) {
        return dayEndTime(time, 0);
    }

    public static long dayEndTime(long time, int dayStartMinute) {
        Calendar calendar = new Calendar.Builder().setInstant(time*1000).build();
        long dayEnd = dayEndTime(calendar, dayStartMinute).toInstant().getEpochSecond();
        return dayEnd >= time ? dayEnd : dayEnd + daySeconds;
    }

    private static Date dayEndTime(Calendar calendar, int dayStartMinute) {
        int h = maxH;
        int m = maxM;
        if (compareIntRangeOpen(dayStartMinute, 0, dayMinutes) == 0) {
            --dayStartMinute;
            h = dayStartMinute / hourMinutes;
            m = dayStartMinute % hourMinutes;
        }
        Calendar cln = (Calendar) calendar.clone();
        cln.set(Calendar.HOUR_OF_DAY, h);
        cln.set(Calendar.MINUTE, m);
        cln.set(Calendar.SECOND, maxS);
        return cln.getTime();
    }

    public static Calendar nextDay(Calendar calendar) {
        return new Calendar.Builder().setInstant(calendar.getTimeInMillis()+daySeconds*1000).build();
    }

    public static int dayCompare(Calendar c1, Calendar c2) {
        int year1 = c1.get(Calendar.YEAR);
        int day1 = c1.get(Calendar.DAY_OF_YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int day2 = c2.get(Calendar.DAY_OF_YEAR);
        if (year1 < year2) {
            return -1;
        } else if (year2 < year1) {
            return 1;
        } else if (day1 < day2) {
            return -1;
        } else if (day2 < day1) {
            return 1;
        } else {
            return 0;
        }
    }

    public static long nextDayBeginTime(long time) {
        Calendar calendar = new Calendar.Builder().setInstant((time+daySeconds)*1000).build();
        calendar.set(Calendar.HOUR_OF_DAY, minH);
        calendar.set(Calendar.MINUTE, minM);
        calendar.set(Calendar.SECOND, minS);
        return calendar.toInstant().getEpochSecond();
    }

    public static Date nextDayBeginTime(Date date) {
        long time = nextDayBeginTime(date.toInstant().getEpochSecond());
        return new Date(time*1000);
    }

    public static long prevDayEndTime(long time) {
        Calendar calendar = new Calendar.Builder().setInstant((time-daySeconds)*1000).build();
        calendar.set(Calendar.HOUR_OF_DAY, maxH);
        calendar.set(Calendar.MINUTE, maxM);
        calendar.set(Calendar.SECOND, maxS);
        return calendar.toInstant().getEpochSecond();
    }

    public static boolean isDayBegin(long time, int dayStartMinute) {
        int h = minH;
        int m = minM;
        if (compareIntRangeOpen(dayStartMinute, 0, dayMinutes) == 0) {
            h = dayStartMinute / hourMinutes;
            m = dayStartMinute % hourMinutes;
        }
        Calendar calendar = new Calendar.Builder().setInstant(time*1000).build();
        return calendar.get(Calendar.HOUR_OF_DAY) == h
               && calendar.get(Calendar.MINUTE) == m
               && calendar.get(Calendar.SECOND) == minS;
    }

    public static long shapeDayBegin(long time) {
        return isDayEnd(time, 0) ? ++time : time;
    }

    public static long shapeDayBegin(long time, int dayStartMinute) {
        return isDayEnd(time, dayStartMinute) ? ++time : time;
    }

    public static boolean isDayEnd(long time, int dayStartMinute) {
        int h = maxH;
        int m = maxM;
        if (compareIntRangeOpen(dayStartMinute, 0, dayMinutes) == 0) {
            --dayStartMinute;
            h = dayStartMinute / hourMinutes;
            m = dayStartMinute % hourMinutes;
        }
        Calendar calendar = new Calendar.Builder().setInstant(time*1000).build();
        return calendar.get(Calendar.HOUR_OF_DAY) == h
               && calendar.get(Calendar.MINUTE) == m
               && calendar.get(Calendar.SECOND) == maxS;
    }

    public static long shapeDayEnd(long time) {
        return shapeDayEnd(time, 0);
    }

    public static long shapeDayEnd(long time, int dayStartMinute) {
        return isDayBegin(time, dayStartMinute) ? --time : time;
    }

    public static int numberOfContinuousDay(long begin, long end, int time) {
        if (end <= begin) {
            return 0;
        } else {
            long dayEnd = dayEndTime(begin, time);
            if (dayEnd < end) {
                return lengthToUnit((int)(end - dayEnd), daySeconds);
            } else {
                return 0;
            }
        }
    }

    public static boolean round(long time1, long time2) {
        return (Long.max(time1, time2) - Long.min(time1, time2)) <= 1;
    }

    public static int minuteOffsetInDay(long time) {
        return secondOffsetInDay(time) / minuteSeconds;
    }

    public static int secondOffsetInDay(long time) {
        Calendar calendar = new Calendar.Builder().setInstant(time*1000).build();
        return calendar.get(Calendar.HOUR_OF_DAY)*hourSeconds+calendar.get(Calendar.MINUTE)*minuteSeconds+calendar.get(Calendar.SECOND);
    }

    public static String timestamp() {
        return timestampFormat.format(currentTime());
    }

    public static long currentEpoch() {
        return currentTime().toInstant().getEpochSecond();
    }

    public static long currentMiliEpoch() {
        return currentTime().getTime();
    }

    public static Date currentTime() {
        return (appProperties.isDebugOn() && now != null) ? now : new Date();
    }

    public static Calendar currentCalendar() {
        return new Calendar.Builder().setInstant(currentTime()).build();
    }

    public static String currentShortYMDHMS() {
        return shortTimeStampFormat.format(currentTime());
    }

    public static String currentYMD() {
        return ymdFormat.format(currentTime());
    }

    public static String currentSimpleYMD() {
        return simpleYmdFormat.format(currentTime());
    }

    public static String currentSimpleYMD(final Date date) {
        return simpleYmdFormat.format(date);
    }
    /**
     * 获取 day 天之后的 年月日
     * @param day 几天后的天数
     * @return YMD 字符串
     */
    public static String afterSimpleYMD(final int day) {
        return simpleYmdFormat.format(new Date((System.currentTimeMillis() / 1000 + (24 * 60 * 60) * day) * 1000));
    }

    public static String afterSimpleYMD(final Date date, final int day) {
        return simpleYmdFormat.format(new Date((date.getTime() / 1000 + (24 * 60 * 60) * day) * 1000));
    }

    public static String currentHMS() {
        return hmsFormat.format(currentTime());
    }

    public static String epochToYMDHMS(long epoch) {
        return simpleDateFormat.format(new Date(epoch*1000));
    }

    public static String currentYMDHMS() {
        return simpleDateFormat.format(currentTime());
    }

    public static long currentSimpleMillis(final String simpleDateString) {
        return simpleDateFormat.parse(simpleDateString, new ParsePosition(0)).getTime() / 1000;
    }

    public static String currentYMDHMSForETC() {
        return etcTimeFormat.format(currentTime());
    }

    public static long expireTime(long minutes) {
        return currentEpoch() + minutes * minuteSeconds;
    }

    public static Integer daySecondSeq() {
        return (int)(currentEpoch() % daySeconds);
    }

    public static int incOrderNo() {
        return orderSeq.incrementAndGet();
    }

    public static String makeOrderNo(String projectNo, String termNo) {
        if (termNo.length() > 3) {
            termNo = termNo.substring(termNo.length() - 3, termNo.length());
        }
        return timestamp() + projectNo + termNo + to4bitHex(incOrderNo()).toUpperCase() + "000";
    }

    public static int incPayNo() {
        return paySeq.incrementAndGet();
    }

    public static String shortUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static int shape(int n, int min, int max) {
        if (n < min) {
            return min;
        } else if (n > max) {
            return max;
        } else {
            return n;
        }
    }

    public static int rate(int value, int rate) {
        return value*rate/100;
    }

    public static String toHexString(byte data) {
        return toHexString(new byte[]{data});
    }

    public static String to4bitHex(int count) {
        return Integer.toHexString(count & 0x0F);
    }

    public static String toHexString(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b: data) {
            String hv = Integer.toHexString(b & 0xFF);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 根据时分秒 两个字符串比较大小
     * eg: 02:00:00 20:00:00
     * @param startTime {@link String} 开始时间
     * @param endTime {@link String} 结束时间
     * @return {@link Boolean}
     */
    public static Boolean compareToTime(final String startTime, final String endTime) {
        if (!startTime.contains(":") || !endTime.contains(":")) {
            log.error("比较时间字符串格式不正确,正确格式: => xx:xx:xx");
        } else {
            String[] startArray = startTime.split(":");
            int startTotal = (Integer.parseInt(startArray[0]) * 3600) + (Integer.parseInt(startArray[1]) * 60) + Integer
                    .parseInt(startArray[2]);
            String[] endArray = endTime.split(":");
            int endTotal = (Integer.parseInt(endArray[0]) * 3600) + (Integer.parseInt(endArray[1]) * 60) + Integer
                    .parseInt(endArray[2]);
            return startTotal - endTotal > 0;
        }
        return false;
    }

    /**
     *  将时间 hh:MM:ss 转成当前的 秒数
     * @param addBegin add begin
     * @param addEnd add end
     * @param currentDate current date
     * @return {@link List}
     */
    public static List<Long> timeToSecond(final Date currentDate, final String addBegin, final String addEnd) {
        if (!ObjectUtils.isEmpty(addBegin) && !ObjectUtils.isEmpty(addEnd)) {
            List<String> result =  connectDateTime(currentDate, addBegin, addEnd);
            if (StringUtils.isNotEmpty(result.get(0)) && StringUtils.isNotEmpty(result.get(1))) {
                log.info("累加时间段，拼接之后针对当前周期时间为:" + result.get(0) + " " + result.get(1));
                List<Long> currentDateSeconds = new ArrayList<>(2);
                currentDateSeconds.add(Util.currentSimpleMillis(result.get(0)));
                currentDateSeconds.add(Util.currentSimpleMillis(result.get(1)));
                return currentDateSeconds;
            }
        }
        return  new ArrayList<>();
    }
    /**
     * 根据停车时长和计费时间段计算出费用
     * @param minutes parking minutes
     * @param multiParkingUnit amount units info
     * @return the amount
     */
    public static int multiParkingAmount(int minutes, MultiParkingUnit multiParkingUnit) {
        int amount = 0;
        if (!ObjectUtils.isEmpty(multiParkingUnit) && !multiParkingUnit.getAmountPerUnits().isEmpty() && minutes > 0) {
            List<AmountPerUnits> amountPerUnitsList = multiParkingUnit.getAmountPerUnits();
            int rangeFreeMinutes = 0;
            if (multiParkingUnit.getRangeFreeMinutes() > 0) {
                if (multiParkingUnit.getRangeFreeMinutes() >= minutes) {
                    rangeFreeMinutes = minutes;
                } else {
                    rangeFreeMinutes = multiParkingUnit.isRangeFreeInCharge() ? 0 : multiParkingUnit.getRangeFreeMinutes();
                }
            }
           minutes -= rangeFreeMinutes;
           for (AmountPerUnits amountPerUnits : amountPerUnitsList) {
               if (minutes >= amountPerUnits.getTotalUnitLength()) {
                   amount += amountPerUnits.getTotalUnitLength() / amountPerUnits.getUnitLength() * amountPerUnits.getAmountPerUnit();
                   minutes -= amountPerUnits.getTotalUnitLength();
               } else {
                   if (minutes > multiParkingUnit.getMinUnitLength()) {
                       amount += (minutes / amountPerUnits.getUnitLength() +
                               ((minutes % amountPerUnits.getUnitLength() > multiParkingUnit.getMinUnitLength()) ? 1 : 0)) *
                               amountPerUnits.getAmountPerUnit();
                   }
                   break;
               }
           }
        }
        return  amount;
    }

    /**
     * TODO: 判断可使用时间段是否在范围内
     * @return {@link Boolean}
     */
    public static boolean expiredTime(final String usedStartTime, final String usedEndTime) {
        // 字段合法性判断
        if (!ObjectUtils.isEmpty(usedStartTime) && !ObjectUtils.isEmpty(usedEndTime)) {
            List<String> result = connectDateTime(Util.currentTime(),usedStartTime, usedEndTime);
            if (StringUtils.isNotEmpty(result.get(0)) && StringUtils.isNotEmpty(result.get(1))) {
                log.info("判断使用时间段,拼接之后时间:" + result.get(0) + "," + result.get(1));
                // 将组织新新时间进行比较
                long usedStartSeconds = Util.currentSimpleMillis(result.get(0));
                long usedEndSeconds = Util.currentSimpleMillis(result.get(1));
                long currentSeconds = Util.currentEpoch();
                return (currentSeconds >= usedStartSeconds && currentSeconds <= usedEndSeconds);
            }
        }
        return true;
    }

    /**
     * 未匹配强制开闸时间配置
     * @param startTime the start time
     * @param endTime the end time
     * @return boolean
     */
    public static boolean unMatchForceOpenExpiredTime(final String startTime, final String endTime) {
        if (!ObjectUtils.isEmpty(startTime) && !ObjectUtils.isEmpty(endTime)) {
            List<String> result = connectDateTime(Util.currentTime(),startTime, endTime);
            if (StringUtils.isNotEmpty(result.get(0)) && StringUtils.isNotEmpty(result.get(1))) {
                log.info("强制开闸判断使用时间段,拼接之后时间:" + result.get(0) + "," + result.get(1));
                // 将组织新新时间进行比较
                long usedStartSeconds = Util.currentSimpleMillis(result.get(0));
                long usedEndSeconds = Util.currentSimpleMillis(result.get(1));
                long currentSeconds = Util.currentEpoch();
                return (currentSeconds >= usedStartSeconds && currentSeconds <= usedEndSeconds);
            }
        }
        return false;
    }

    /**
     * 抽离公用方法
     * @param usedStartTime the start time
     * @param usedEndTime the end time
     * @return {@link List}
     */
    private static List<String> connectDateTime(final Date currentDate, final String usedStartTime, final String usedEndTime) {
        List<String> result = new ArrayList<>(2);
        if (Util.compareToTime(usedStartTime, usedEndTime)) {
            // 此处说明 开始时间大于 结束时间,说明跨夜
            result.add(0, Util.currentSimpleYMD(currentDate) + " " + usedStartTime);
            result.add(1, Util.afterSimpleYMD(currentDate, 1) + " " + usedEndTime);
        } else {
            result.add(0, Util.currentSimpleYMD(currentDate) + " " + usedStartTime);
            result.add(1, Util.currentSimpleYMD(currentDate) + " " + usedEndTime);
        }
        return result;
    }
}
