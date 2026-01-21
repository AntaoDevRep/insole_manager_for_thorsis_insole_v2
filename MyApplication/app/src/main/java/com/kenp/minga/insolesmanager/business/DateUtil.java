package com.kenp.minga.insolesmanager.business;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by minga on 11/16/2017.
 */

public class DateUtil {
    public static final long nd = 1000 * 24 * 60 * 60;// milliseconds of one day
    public static final long nh = 1000 * 60 * 60;// milliseconds of one hour
    public static final long nm = 1000 * 60;// milliseconds of one minute
    public static final long ns = 1000;// milliseconds of one second
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //get the difference in days between two date time
    public static long dateDiffInDay(Date startTime, Date endTime) {
        long diff;
        long day = 0;
        try {
            diff = endTime.getTime()
                    - startTime.getTime();
            //calculate the difference of day
            day = diff / nd;
            System.out.println("Time difference：" + day + "day" );
            if (day >= 0) {
                return day;
            }else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * calculates the difference in hours between two date times.
     * @param startTime Start time (from)
     * @param endTime time difference will be until this date.
     * @return The difference in hours.
     */

    public static long dateDiffInHour(Date startTime, Date endTime) {
        long diff;
        long hour;
        try {
            if (endTime.after(startTime)){
                diff = endTime.getTime()
                        - startTime.getTime();
            } else {
                diff = startTime.getTime()
                        - endTime.getTime();
            }
            //calculate the difference of hour, min, sec
            hour = diff / nh;
            System.out.println("Time difference in hours：" + hour);
            if (hour >= 0) {
                return hour;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //get the difference in minutes between two date time
    public static long dateDiffInMin(Date startTime, Date endTime) {
        long diff;
        long min = 0;
        try {
            diff = endTime.getTime()
                    - startTime.getTime();
            //calculate the difference in minutes
            min = diff / nm;
            System.out.println("Time difference in min：" + min);
            if (min >= 1) {
                return min;
            }else {
                if (min == 0) {
                    return 1;
                }else {
                    return 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    //get the difference in seconds between two date time
    public static long dateDiffInSec(Date startTime, Date endTime) {
        long diff;
        long sec = 0;
        try {
            diff = endTime.getTime()
                    - startTime.getTime();
            //calculate the difference in sec
            sec = diff / ns;
            System.out.println("Time difference in sec：" + sec);
            if (sec >= 1) {
                return sec;
            }else {
                if (sec == 0) {
                    return 1;
                }else {
                    return 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    //get the difference in milli seconds between two date time
    public static long dateDiffInMillSec(Date startTime, Date endTime) {
        long diff;
        try {
            diff = endTime.getTime()
                    - startTime.getTime();
            //calculate the difference in sec
            System.out.println("Time difference in milli sec：" + diff);
            return diff;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //compare two dates
    public static int compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 is before dt2");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1 is after dt2");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static boolean sameDay (Date date1, Date date2){
        if (date1 == null || date2 == null){
            return false;
        } else {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return  (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH));
        }
    }

    public static boolean isToday(Date dateToCheck){
        if (dateToCheck == null){
            return false;
        } else {
            Date currentDate = new Date(System.currentTimeMillis());
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(currentDate);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(dateToCheck);
            return  (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH));
        }
    }

    public static boolean isYesterday(Date dateToCheck){
        if (dateToCheck == null){
            return false;
        } else {
            Date currentDate = new Date(System.currentTimeMillis());
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(currentDate);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(dateToCheck);

            if (cal1.get(Calendar.YEAR) == (cal2.get(Calendar.YEAR))) {
                int diffDay = cal1.get(Calendar.DAY_OF_YEAR)
                        - cal2.get(Calendar.DAY_OF_YEAR);

                if (diffDay == 1) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isTomorrow(Date dateToCheck){
        if (dateToCheck == null){
            return false;
        } else {
            Date currentDate = new Date(System.currentTimeMillis());
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(currentDate);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(dateToCheck);

            if (cal1.get(Calendar.YEAR) == (cal2.get(Calendar.YEAR))) {
                int diffDay = cal1.get(Calendar.DAY_OF_YEAR)
                        - cal2.get(Calendar.DAY_OF_YEAR);

                if (diffDay == -1) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String dateToString(Date data) {
        return new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.GERMANY).format(data);
    }

    public static Date stringToDate(String strTime)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.GERMANY);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
}
