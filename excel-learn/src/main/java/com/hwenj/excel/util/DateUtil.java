package com.hwenj.excel.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 时间工具类
 */
@Slf4j
public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";
    public static final String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_START = "yyyy-MM-dd 00:00:00";
    public static final String DATE_TIME_END = "yyyy-MM-dd 23:59:59";
    public static final String POINT_YYYYMM = ".yyyyMM";
    public static final String POINT_YYYYMMDD = ".yyyyMMdd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String DATE_FORMAT_SPLITE = "yyyy.MM.dd";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String formatFullTime(LocalDateTime localDateTime) {
        return formatFullTime(localDateTime, FULL_TIME_PATTERN);
    }

    public static String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simformat = new SimpleDateFormat(dateFormatType);
        return simformat.format(date);
    }

    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }

    public static String formatLongToString(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(timestamp+"000")));
    }

    public static Date addTime(Date date,int length,String unit){
        SimpleDateFormat sdf = new SimpleDateFormat(FULL_TIME_SPLIT_PATTERN);  //构造格式化日期的格式
        Calendar cd = Calendar.getInstance();

        cd.setTime(date);

        if(unit.equals("1")){
            cd.add(Calendar.YEAR, length);//增加一年
        }else if(unit.equals("2")){
            cd.add(Calendar.MONTH, length);//n=1代表增加一个月
        } else if(unit.equals("3")){
            cd.add(Calendar.DAY_OF_MONTH, length);//n=1代表增加一天
        }
        return cd.getTime();  //format(Date date)方法：将制定的日期对象格式，化为指定格式的字符串（例如：“yyyy-MM-dd”）
    }

    public static int getDiscrepantDays(String dateStart, String dateEnd) {
        return (int) ((getFormatDate(dateEnd).getTime() - getFormatDate(dateStart).getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(String d1, String d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(getFormatDate(d1));
        c2.setTime(getFormatDate(d2));
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    /**
     * 获取两个日期相差的月数 yyyy-mm-dd
     */
    public static int getMonthDiffFormat(String d1, String d2, String format) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(getFormatDate(d1, format));
        c2.setTime(getFormatDate(d2, format));
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    public static Date getFormatDate(String currDate) {
        return getFormatDate(currDate, FULL_TIME_SPLIT_PATTERN);
    }
    /**
     * 根据格式得到格式化后的日期
     *
     * @param currDate
     *                要格式化的日期
     * @param format
     *                日期格式，如yyyy-MM-dd
     * @see SimpleDateFormat#parse(String)
     * @return Date 返回格式化后的日期，格式由参数<code>format</code>
     *         定义，如yyyy-MM-dd，如2006-02-15
     */
    public static Date getFormatDate(String currDate, String format) {
        if (currDate == null) {
            return null;
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static boolean validateTimeStamp(Long timestamp) throws Exception{
        Date dt=new Date();
      /*  if(timestamp > dt.getTime()/1000+600){
            //丢弃未来的时间戳
            return false;
        }*/
        if((dt.getTime()/1000-timestamp)/60>10){
            //验证时间戳是否超过十分钟
            return false;
        }
        return true;
    }

    public static int compareDate(String DATE1, String DATE2) {
      //  DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    /**
     * 解析字符串日期 异常返回null
     * @param d
     * @param format
     * @return
     */
    public static Date parseDate(String d,String format) {
        try {
            return new SimpleDateFormat(format).parse(d);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String stap){
        String time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(stap);
        Date date = new Date(lt);
        time = simpleDateFormat.format(date);
        return time;
    }

    /**
     *  字符串日期转unix时间戳
     * @param time
     * @return
     */
    public static long convertTimeToUnixLong(String time, boolean isStart){
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FULL_TIME_SPLIT_PATTERN);
        Date date = null;
        try {
            if(!isStart){
                time = time + " 23:59:59";
            }else{
                time = time + " 00:00:00";
            }
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime()/1000;
    }

    /**
     *  localDate转Date
     *
     */
    public static Date localDate2Date(LocalDate localDate) {
        if(null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }
    /**
     * 获取前n天的日期 yyyy-MM-dd
     */
    public static String getDate(int i){
        return LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取前n天时间的时间戳
     */
    public static long getTimestamp(int i){
        String format = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return convertTimeToUnixLong(format, true);
    }

    public static String getFullDate(int i){
        return LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'"));
    }

    /**
     * 查询日期集合
     */
    public static String[] getDateList(String format,int size){
        List<String> days = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String day = LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern(format));
            days.add(day);
        }
        return days.toArray(new String[days.size()]);
    }

    public static void main(String[] args) {
        String formatPattern = "yyyy-MM-dd HH:mm:ss";
        String utc = "2019-07-10T16:00:00.000Z";
        ZonedDateTime zdt  = ZonedDateTime.parse(utc);
        LocalDateTime localDateTime = zdt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        String gst = formatter.format(localDateTime.plusHours(8));
        System.out.println(gst);

        DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String a = "2022-03-31";

        Date Date1=null;
        Date Date2=null;
        //1.	设置两个时间
        try {
            Date1 = DateUtils.parseDate(a,"yyyy-MM-dd");
            Date2 = DateUtils.parseDate("2018-07-02 18:35:53","yyyy-MM-dd HH:mm:ss");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (DateUtil.compareDate(DateFormatUtils.format(Date1, "yyyy-MM-dd"),  DateFormatUtils.format(new Date(), "yyyy-MM-dd")) == 1 ) {

        }


    }



    public static Date dateTime2Date(Date dateTime)
    {
        Date date = dateTime;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String s = sdf.format(dateTime);
        try {
            date =  sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     *
     * 自定义取值，Date类型转为String类型
     *
     * @param date 日期
     * @param pattern 格式化常量
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String dateToStr(Date date, String pattern)
    {
        SimpleDateFormat format = null;
        if (null == date) {
            return null;
        }
        format = new SimpleDateFormat(pattern, Locale.getDefault());

        return format.format(date);
    }

    /**
     * 将字符串转换成Date类型的时间
     * <hr>
     *
     * @param s 日期类型的字符串<br>
     *            datePattern :YYYY_MM_DD<br>
     * @return java.util.Date
     */
    public static Date strToDate(String s, String pattern)
    {
        if (s == null)
        {
            return null;
        }
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try
        {
            date = sdf.parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateTime3Date(Date dateTime) {
        Date date = dateTime;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SPLITE);
        String s = sdf.format(dateTime);
        try {
            date =  sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateTime4Date(Date dateTime) {
        Date date = dateTime;
        SimpleDateFormat sdf = new SimpleDateFormat(FULL_TIME_SPLIT_PATTERN);
        String s = sdf.format(dateTime);
        try {
            date =  sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateTime2DateStr(Date dateTime)
    {
        return new SimpleDateFormat(DATE_FORMAT).format(dateTime);
    }
}
