package com.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
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
import java.util.Enumeration;
import java.util.Hashtable;
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
     * 获取指定时间戳所在月份开始的时间戳/秒
     * @param dateTimeMillis 指定时间戳/毫秒
     * @return
     */
    public static Long getMonthBegin(Long dateTimeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(dateTimeMillis));

        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis() / 1000;
    }

    /**
     * 获取指定时间戳所在月份结束的时间戳/秒
     * @param dateTimeMillis 指定时间戳/毫秒
     * @return
     */
    public static Long getMonthEnd(Long dateTimeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(dateTimeMillis));

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis() / 1000;
    }

    public static boolean isDate(String dateString, String format) {
        if (dateString == null) {
            return false;
        } else if (dateString.length() != format.length()) {
            return false;
        } else {
            try {
                stringToDate(dateString, format);
                return true;
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public static String dateToString(Date date, String format) throws Exception {
        if (date == null) {
            return null;
        } else {
            if (format == null || format.equalsIgnoreCase("")) {
            }

            Hashtable<Integer, String> h = new Hashtable();
            String javaFormat = new String();
            if (format.indexOf("yyyy") != -1) {
                h.put(new Integer(format.indexOf("yyyy")), "yyyy");
            } else if (format.indexOf("yy") != -1) {
                h.put(new Integer(format.indexOf("yy")), "yy");
            }

            if (format.indexOf("MM") != -1) {
                h.put(new Integer(format.indexOf("MM")), "MM");
            } else if (format.indexOf("mm") != -1) {
                h.put(new Integer(format.indexOf("mm")), "MM");
            }

            if (format.indexOf("dd") != -1) {
                h.put(new Integer(format.indexOf("dd")), "dd");
            }

            if (format.indexOf("hh24") != -1) {
                h.put(new Integer(format.indexOf("hh24")), "HH");
            } else if (format.indexOf("hh") != -1) {
                h.put(new Integer(format.indexOf("hh")), "HH");
            } else if (format.indexOf("HH") != -1) {
                h.put(new Integer(format.indexOf("HH")), "HH");
            }

            if (format.indexOf("mi") != -1) {
                h.put(new Integer(format.indexOf("mi")), "mm");
            } else if (format.indexOf("mm") != -1 && h.containsValue("HH")) {
                h.put(new Integer(format.lastIndexOf("mm")), "mm");
            }

            if (format.indexOf("ss") != -1) {
                h.put(new Integer(format.indexOf("ss")), "ss");
            }

            if (format.indexOf("SSS") != -1) {
                h.put(new Integer(format.indexOf("SSS")), "SSS");
            }

            int i;
            for(i = 0; format.indexOf("-", i) != -1; ++i) {
                i = format.indexOf("-", i);
                h.put(new Integer(i), "-");
            }

            for(i = 0; format.indexOf(".", i) != -1; ++i) {
                i = format.indexOf(".", i);
                h.put(new Integer(i), ".");
            }

            for(i = 0; format.indexOf("/", i) != -1; ++i) {
                i = format.indexOf("/", i);
                h.put(new Integer(i), "/");
            }

            for(i = 0; format.indexOf(" ", i) != -1; ++i) {
                i = format.indexOf(" ", i);
                h.put(new Integer(i), " ");
            }

            for(i = 0; format.indexOf(":", i) != -1; ++i) {
                i = format.indexOf(":", i);
                h.put(new Integer(i), ":");
            }

            if (format.indexOf("年") != -1) {
                h.put(new Integer(format.indexOf("年")), "年");
            }

            if (format.indexOf("月") != -1) {
                h.put(new Integer(format.indexOf("月")), "月");
            }

            if (format.indexOf("日") != -1) {
                h.put(new Integer(format.indexOf("日")), "日");
            }

            if (format.indexOf("时") != -1) {
                h.put(new Integer(format.indexOf("时")), "时");
            }

            if (format.indexOf("分") != -1) {
                h.put(new Integer(format.indexOf("分")), "分");
            }

            if (format.indexOf("秒") != -1) {
                h.put(new Integer(format.indexOf("秒")), "秒");
            }

            String temp;
            for(boolean var9 = false; h.size() != 0; javaFormat = temp + javaFormat) {
                Enumeration<Integer> e = h.keys();
                int n = 0;

                while(e.hasMoreElements()) {
                    i = (Integer)e.nextElement();
                    if (i >= n) {
                        n = i;
                    }
                }

                temp = (String)h.get(new Integer(n));
                h.remove(new Integer(n));
            }

            SimpleDateFormat df = new SimpleDateFormat(javaFormat, new DateFormatSymbols());
            return df.format(date);
        }
    }

    public static Date stringToDate(String dateString, String format) throws Exception {
        if (dateString == null) {
            return null;
        } else {
            if (dateString.equalsIgnoreCase("")) {
            }

            if (format == null || format.equalsIgnoreCase("")) {
            }

            Hashtable<Integer, String> h = new Hashtable();
            if (format.indexOf("yyyy") != -1) {
                h.put(new Integer(format.indexOf("yyyy")), "yyyy");
            } else if (format.indexOf("yy") != -1) {
                h.put(new Integer(format.indexOf("yy")), "yy");
            }

            if (format.indexOf("MM") != -1) {
                h.put(new Integer(format.indexOf("MM")), "MM");
            } else if (format.indexOf("mm") != -1) {
                h.put(new Integer(format.indexOf("mm")), "MM");
            }

            if (format.indexOf("dd") != -1) {
                h.put(new Integer(format.indexOf("dd")), "dd");
            }

            if (format.indexOf("hh24") != -1) {
                h.put(new Integer(format.indexOf("hh24")), "HH");
            } else if (format.indexOf("hh") != -1) {
                h.put(new Integer(format.indexOf("hh")), "HH");
            } else if (format.indexOf("HH") != -1) {
                h.put(new Integer(format.indexOf("HH")), "HH");
            }

            if (format.indexOf("mi") != -1) {
                h.put(new Integer(format.indexOf("mi")), "mm");
            } else if (format.indexOf("mm") != -1 && h.containsValue("HH")) {
                h.put(new Integer(format.lastIndexOf("mm")), "mm");
            }

            if (format.indexOf("ss") != -1) {
                h.put(new Integer(format.indexOf("ss")), "ss");
            }

            if (format.indexOf("SSS") != -1) {
                h.put(new Integer(format.indexOf("SSS")), "SSS");
            }

            int intStart;
            for(intStart = 0; format.indexOf("-", intStart) != -1; ++intStart) {
                intStart = format.indexOf("-", intStart);
                h.put(new Integer(intStart), "-");
            }

            for(intStart = 0; format.indexOf(".", intStart) != -1; ++intStart) {
                intStart = format.indexOf(".", intStart);
                h.put(new Integer(intStart), ".");
            }

            for(intStart = 0; format.indexOf("/", intStart) != -1; ++intStart) {
                intStart = format.indexOf("/", intStart);
                h.put(new Integer(intStart), "/");
            }

            for(intStart = 0; format.indexOf(" ", intStart) != -1; ++intStart) {
                intStart = format.indexOf(" ", intStart);
                h.put(new Integer(intStart), " ");
            }

            for(intStart = 0; format.indexOf(":", intStart) != -1; ++intStart) {
                intStart = format.indexOf(":", intStart);
                h.put(new Integer(intStart), ":");
            }

            if (format.indexOf("年") != -1) {
                h.put(new Integer(format.indexOf("年")), "年");
            }

            if (format.indexOf("月") != -1) {
                h.put(new Integer(format.indexOf("月")), "月");
            }

            if (format.indexOf("日") != -1) {
                h.put(new Integer(format.indexOf("日")), "日");
            }

            if (format.indexOf("时") != -1) {
                h.put(new Integer(format.indexOf("时")), "时");
            }

            if (format.indexOf("分") != -1) {
                h.put(new Integer(format.indexOf("分")), "分");
            }

            if (format.indexOf("秒") != -1) {
                h.put(new Integer(format.indexOf("秒")), "秒");
            }

            String javaFormat = new String();

            String temp;
            for(boolean var4 = false; h.size() != 0; javaFormat = temp + javaFormat) {
                Enumeration<Integer> e = h.keys();
                int n = 0;

                while(e.hasMoreElements()) {
                    int i = (Integer)e.nextElement();
                    if (i >= n) {
                        n = i;
                    }
                }

                temp = (String)h.get(new Integer(n));
                h.remove(new Integer(n));
            }

            SimpleDateFormat df = new SimpleDateFormat(javaFormat);
            df.setLenient(false);
            Date myDate = new Date();

            try {
                myDate = df.parse(dateString);
            } catch (ParseException var10) {
                try {
                    df.setLenient(true);
                    Calendar c = Calendar.getInstance();
                    c.setTime(df.parse(dateString));
                    if ((c.get(1) != 1991 || c.get(2) != 3 || c.get(5) != 14 || c.get(11) != 1) && (c.get(1) != 1990 || c.get(2) != 3 || c.get(5) != 15 || c.get(11) != 1) && (c.get(1) != 1989 || c.get(2) != 3 || c.get(5) != 16 || c.get(11) != 1) && (c.get(1) != 1988 || c.get(2) != 3 || c.get(5) != 10 || c.get(11) != 1) && (c.get(1) != 1987 || c.get(2) != 3 || c.get(5) != 12 || c.get(11) != 1) && (c.get(1) != 1986 || c.get(2) != 4 || c.get(5) != 4 || c.get(11) != 1) && (c.get(1) != 1941 || c.get(2) != 2 || c.get(5) != 16 || c.get(11) != 1) && (c.get(1) != 1940 || c.get(2) != 5 || c.get(5) != 3 || c.get(11) != 1)) {
                    } else {
                        myDate = c.getTime();
                    }
                } catch (ParseException var9) {
                }
            }

            return myDate;
        }
    }

    public static  String lastSecond(String date)  {
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d2=format.parse(date);
            int dayMis=1000*60*60*24;//一天的毫秒-1
            //返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            long curMillisecond=d2.getTime();//当天的毫秒
            long resultMis=curMillisecond+(dayMis-1); //当天最后一秒
            DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //得到我须要的时间    当天最后一秒
            Date resultDate=new Date(resultMis);
            String str = format2.format(resultDate);
            return str;
        }catch (ParseException e){
            return date;
        }
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
    }

}
