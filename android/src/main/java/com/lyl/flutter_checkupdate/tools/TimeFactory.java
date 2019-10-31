package com.lyl.flutter_checkupdate.tools;

import android.content.Context;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lyl on 2016/4/20.
 */
public class TimeFactory {
    private static SimpleDateFormat SDF = new SimpleDateFormat();
    public static final String EEE_dd_MMM_yyyy_KK_mma = "EEE, dd MMM yyyy, KK:mm a";
    public static final String EEE_dd_MMM_yyyy_hh_mma = "EEE, dd MMM yyyy, hh:mm a";
    public static final String dd_MM_yyyy = "dd/MM/yyyy";
    public static final String dd_MM_yyyy_HH_mm = "dd/MM/yyyy HH:mm";
    public static final String MMM_dd_yyyy = "MMM,dd,yyyy";
    public static final String MMM_dd_yyyy_hh_mma = "MMM,dd,yyyy hh:mm a";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyMMddHHmmss = "yyMMddHHmmss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String KK_mm_a = "KK:mm a";
    public static final String hh_mm_a = "hh:mm a";
    public static final String HH_MM = "hh:mm";
    public static final String hh_mm_ss = "HH:mm:ss";
    public static final TimeZone TIME_ZONE = TimeZone
            .getTimeZone("Asia/Shanghai");

    public static String parseToYYYYMMDD(Date date) {
        SDF = new SimpleDateFormat(yyyy_MM_dd);
        SDF.setTimeZone(TIME_ZONE);
        return SDF.format(date);
    }

    public static Date parseYYYYMMDDToDate(String date) {
        SDF = new SimpleDateFormat(yyyy_MM_dd);
        SDF.setTimeZone(TIME_ZONE);
        try {
            return SDF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseToHHMMSS(Date date) {
        SDF = new SimpleDateFormat(hh_mm_ss);
        SDF.setTimeZone(TIME_ZONE);
        return SDF.format(date);
    }

    public static Date parseToDate(String value) {
        try {
            SDF = new SimpleDateFormat(yyMMddHHmmss);
            //SDF.applyPattern(yyMMddHHmmss);
            return SDF.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String parseToyyyy_MM_dd_HH_mm_ss(Date date) {
        if (date != null) {
            SDF = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
            SDF.setTimeZone(TIME_ZONE);
            return SDF.format(date);
        }
        return null;
    }

    public static String partseToyyyy_MM_dd_HH_mm_ss(String date) {
        if (date != null) {
            return "20" + date.substring(0, 2) + "-" + date.substring(2, 4) + "-" + date.substring(4, 6) + " " + date.substring(6, 8) + ":" + date.substring(8, 10) + ":" + date.substring(10, 12);
        }
        return null;
    }

    public static Date parseToDateyyyy_MM_dd_HH_mm_ss(String value) {
        try {
            SDF = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
            //SDF.applyPattern(yyyyMMddHHmmss);
            return SDF.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parseToDateyyyyMMddHHmmss(String value) {
        try {
            SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //SDF.applyPattern(yyyyMMddHHmmss);
            return SDF.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static String parseToYYYYMMDD(String time, String split) {
        String result = time.substring(0, 8);
        result = result.substring(0, 4).concat(split).concat(result.substring(4, 6)).concat(split).concat(result.substring(6, 8));
        return result;
    }

    public static String parseToYYYYMMDD(String time) {
        return parseToYYYYMMDD(time, "-");
    }


    public static int daysOfTwo(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }

    public static final int ALL = 0;
    public static final int JSMS = 1;

    /**
     * 仅仅支持23:59:59
     *
     * @param time
     * @param model
     * @return
     */
    public static String s2TimeString(long time, int model) {
        long s = time % 60;
        long m = (time / 60) % 60;
        long h = (time / 60 / 60) % 60;
        String t = "";
        switch (model) {
            case ALL:
                t = i22l(h) + ":" + i22l(m) + ":" + i22l(s);
                break;
            case JSMS:
                t = ((h == 0) ? "" : i22l(h) + ":") + i22l(m) + ":" + i22l(s);
                break;
        }
        return t;
    }

    public static String i22l(long time) {
        return time < 10 ? "0" + time : time + "";
    }

    //系统时间与传过来的时间差  相减  获得天数。
    public static long getDay(String eldTime) {
        long result = 0;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long endtime = d.parse(eldTime).getTime();
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String strTime = d.format(curDate);
            long currentTiem = d.parse(strTime).getTime();
            result = endtime - currentTiem;
         /*  result = (time) / 3600000 / 24;
           long a=result*/
            ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static long getTeleDay(String eldTime) {
        long result = 0;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long endtime = d.parse(eldTime).getTime();
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String strTime = d.format(curDate);
            long currentTiem = d.parse(strTime).getTime();
            result = endtime - currentTiem;
         /*  result = (time) / 3600000 / 24;
           long a=result*/
            ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    //时间比对 当前时间的前五分钟
    public static boolean minCompareCurrentTime(String compareTime) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String curTime = formatter.format(curDate);
        try {
            long currentTime = formatter.parse(curTime).getTime();
            long comTime = formatter.parse(compareTime).getTime();
            long relateveTime = (currentTime - comTime) / 60000;//5分钟
            if (relateveTime <= 5) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    //系统时间与传过来的时间差  相减  获得天数。
    public static long getDiffDay(String endTime) {
        long result = 0;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long endtime = d.parse(endTime).getTime();
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String strTime = d.format(curDate);
            long currentTiem = d.parse(strTime).getTime();
            result = currentTiem - endtime;
            result = result / 24 / 3600 / 1000
         /*  result = (time) / 3600000 / 24;
           long a=result*/;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static long getCompareDay(String endTime) {
        long result = 0;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long endtime = d.parse(endTime).getTime();
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String strTime = d.format(curDate);
            long currentTiem = d.parse(strTime).getTime();
            result = endtime - currentTiem;
            result = result / 24 / 3600 / 1000;
            if (result <= 0)
                return 0;
         /*  result = (time) / 3600000 / 24;
           long a=result*/
            ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    //远程视频 传过来的设备名转换成时间
    public static String parseNameTotime(String name) {
        String time = name.substring(4, 10);
        StringBuffer buffer = new StringBuffer(time);
        buffer.insert(2, ":");
        buffer.insert(5, ":");
        String time1 = new String(buffer);
        return time1;
    }

}
