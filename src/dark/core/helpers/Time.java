package dark.core.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time
{
    /** Returns the current time stamp as both the Data and string of time */
    public static Pair<String, Date> getCurrentTimeStamp()
    {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return new Pair<String, Date>(strDate, now);
    }

    /** Returns the current time stamp as a class of numbers */
    public static TimeData getCurrentTimeData()
    {
        return new TimeData();
    }

    /** Used to stored the data in a class for easy access */
    public static class TimeData
    {
        int hour, min, sec, day, month, year;

        @SuppressWarnings("deprecation")
        public TimeData()
        {
            Date now = new Date();
            this.hour = now.getHours();
            this.min = now.getMinutes();
            this.sec = now.getSeconds();
            this.day = now.getDay();
            this.month = now.getMonth();
            this.year = now.getYear();
        }

        public TimeData(int hour, int min, int sec, int day, int month, int year)
        {
            this.hour = hour;
            this.min = min;
            this.sec = sec;
            this.day = day;
            this.month = month;
            this.year = year;
        }
    }
}
