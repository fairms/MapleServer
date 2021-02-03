package im.cave.ms.tools;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

    public final static long FT_OFFSET = 116444520000000000L + 60 * 60 * 14 * 1000 * 10000L; // 2339-01-01 02:00:00:000
    public final static long DAY = 60L * 60L * 24L * 1000L * 10000L; //nano

    public static long getFileTime(long timestamp) {
        return getFileTime(timestamp, false);
    }

    public static long getFileTime(long timestamp, boolean roundToMinutes) {
        if (roundToMinutes) {
            timestamp = (timestamp / 1000 / 60) * 600000000;
        } else {
            timestamp = timestamp * 10000;
        }
        return timestamp + FT_OFFSET;
    }

    public static long getFileTime(long timestamp, int addDay) {
        long now = System.currentTimeMillis();
        return getFileTime(now + (addDay * DAY / 10000));
    }

    public static long getTimestamp(long filetime) {
        return (filetime - FT_OFFSET) / 10000;
    }


    public static int getTime() {
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        return Integer.parseInt(format);
    }

    public static int getTime(long timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        return Integer.parseInt(format);
    }

    public static int getDate(long timestamp) {
        LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        String format = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Integer.parseInt(format);
    }


    public static String getTimeFromTimestamp(long timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
    }


    public static String getNowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
    }

    public static String getCurrentDate(String dateFormat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static LocalDateTime getDateTime(String dateString, String dateFormat) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
    }

    public static LocalDate getDate(String dateString, String dateFormat) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
    }

    public static int getDate() {
        String format = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return Integer.parseInt(format);
    }

    public static LocalDate getDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String getFormatDate(LocalDate date, String dateFormat) {
        return date.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getFormatDate(LocalDate date) {
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static long getStringToTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmm");
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return -1;
    }

    public static LocalDate getNextMonday() {
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    public static LocalDate getNextMonday(LocalDate date) {
        return date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    public static LocalDate getFirstDayOfNextMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getFirstDayOfNextMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }
}
