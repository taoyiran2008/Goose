package com.taoyr.app.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by taoyiran on 2018/1/24.
 */

public class TimeUtils {
    public static String parseGmtInMs(String time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            return parseGmtInMs(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String parseGmtInMs(long gmt) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gmt);
        Date date = calendar.getTime();

        String prefix = "";
        int daysBetween = daysBetween(date, today);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        if (daysBetween == 1) {
            prefix = "昨天";
        } else if (daysBetween == 0) {
            //prefix = "今天";
            int hoursBetween = hoursBetween(date, today);
            if (hoursBetween > 0 && hoursBetween < 3) {
                return hoursBetween + "小时前";
            } else if (hoursBetween == 0) {
                int minutesBetween = minutesBetween(date, today);
                return minutesBetween + "分钟前";
            } else { // hoursBetween > 3，显示今日7:00
                prefix = "今天";
            }
        } else if (daysBetween == -1) {
            prefix = "明天";
        } else {
            sdf = new SimpleDateFormat("yyyy年MM月dd日");
        }

        return prefix + " " + sdf.format(date);
    }

    public static String parseGmtInMsGeneric(long gmt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gmt);
        Date date = calendar.getTime();

        // 统一日期格式
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    public static String parseGmtInMsGeneric2(long gmt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gmt);
        Date date = calendar.getTime();

        // 统一日期格式
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }

    /**
     * 计算两个日期相差的天数。
     */
    public static int daysBetween(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) { // 跨年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
                    timeDistance += 366;
                } else { // 普通年365天
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else { // 同一年
            return day2 - day1;
        }
    }

    public static int hoursBetween(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
        int hour2 = cal2.get(Calendar.HOUR_OF_DAY);

        return hour2 - hour1;
    }

    public static int minutesBetween(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int minute1 = cal1.get(Calendar.MINUTE);
        int minute2 = cal2.get(Calendar.MINUTE);

        return minute2 - minute1;
    }
}
