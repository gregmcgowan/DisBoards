package com.gregmcgowan.drownedinsound.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;


/**
 * Various methods to provide some useful functionality for dealing with dates
 *
 * @author gmcgowan
 */
public class DateUtils {


    public static final String DIS_BOARD_POST_SUMMARY_LIST_DATE_FORMAT = "dd MMM HH:MM";
    public static final String DIS_BOARD_COMMENT_DATE_FORMAT = "dd MMM yy HH:mm";

    //17:13 April 15th, 2013
    public static final String DIS_BOARD_POST_DATE_FORMAT = "HH:mm dd MMMM yy";
    public static final String DIS_BOARD_LAST_COMMENT_DATE_FORMAT =  "MMMM dd yyyy HH:mm";

    public static final String DATE_FORMAT_YEAR_FIRST = "yyyyMMdd";

    /**
     * dd/MM/yyyy HH:mm:ss
     */
    public static final String DATE_FORMAT_LONG = "dd/MM/yyyy HH:mm:ss";

    /**
     * dd/MM/yyyy HH:mm
     */
    public static final String DATE_FORMAT_MEDIUM = "dd/MM/yyyy HH:mm";

    public static final String DATE_FORMAT_MEDIUM_NO_DATE_SEPARATOR = "ddMMyyyy HH:mm:ss";

    /**
     * dd/MM/yyyy
     */
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";

    /**
     * dd-MMM-yyyy
     */
    public static final String DATE_FORMAT_ALT = "dd-MMM-yyyy";

    /**
     * dd-MMM-yyyy HH:mm
     */
    public static final String DATE_FORMAT_ALT_WITH_TIME = "dd-MMM-yyyy HH:mm";

    /**
     * HH:mm
     */
    public static final String TIME_FORMAT_24 = "HH:mm";

    private static final String[] dateFormats = {DATE_FORMAT_LONG, DATE_FORMAT_MEDIUM, DATE_FORMAT_MEDIUM_NO_DATE_SEPARATOR, DATE_FORMAT_SHORT, DATE_FORMAT_YEAR_FIRST};


    private static Hashtable<String, SimpleDateFormat> formats;

    static {
        formats = new Hashtable<String, SimpleDateFormat>();
        formats.put(DATE_FORMAT_LONG, new SimpleDateFormat(DATE_FORMAT_LONG, Locale.UK));
        formats.put(DATE_FORMAT_MEDIUM, new SimpleDateFormat(DATE_FORMAT_MEDIUM, Locale.UK));
        formats.put(DATE_FORMAT_SHORT, new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.UK));
        formats.put(DATE_FORMAT_ALT, new SimpleDateFormat(DATE_FORMAT_ALT, Locale.UK));
        formats.put(DATE_FORMAT_ALT_WITH_TIME, new SimpleDateFormat(DATE_FORMAT_ALT_WITH_TIME, Locale.UK));
        formats.put(TIME_FORMAT_24, new SimpleDateFormat(TIME_FORMAT_24, Locale.UK));
    }

    public static Date parseDate(String date, String dateFormat) {
        SimpleDateFormat format = getOrCreate(dateFormat);
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static SimpleDateFormat getOrCreate(String dateFormat) {
        SimpleDateFormat format =  formats.get(dateFormat);
        if (format == null) {
            format = new SimpleDateFormat(dateFormat, Locale.UK);
            formats.put(dateFormat, format);
        }
        return format;
    }

    public static String formatDate(String dateFormat, Date date) {
        String formattedDate = null;

        if (dateFormat != null && date != null) {
            SimpleDateFormat formatter = getOrCreate(dateFormat);
            synchronized (formatter) {
                formattedDate = formatter.format(date);
            }
        }

        return formattedDate;
    }

    /**
     * Returns positive if date2 is older than date1.
     * Negative if date1 is  older than date2. 0 if
     * the dates are the same
     *
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return
     */
    public static int compare(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();

        if (time1 > time2) {
            return 1;
        } else if (time1 < time2) {
            return -1;
        }

        return 0;
    }

    /**
     * @param dayOfMonth 1-31
     * @param monthNo    1-12
     * @param year       Full 4 digit year
     * @return
     */
    public static Date getDate(int dayOfMonth, int monthNo, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, monthNo - 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * Add a number of months to the given date and return a date object
     * with the appropriate values. Negative values wil be handled
     */
    public static Date addMonthsToDate(final Date date, int noOfMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date.getTime()));
        int existingMonth = calendar.get(Calendar.MONTH);
        int newMonth = existingMonth + noOfMonths;
        int year = calendar.get(Calendar.YEAR);
        if (newMonth > 11) {
            newMonth = newMonth - 11;
            year++;
        } else if (newMonth < 0) {
            newMonth = 12 + newMonth;
            year--;
        }
        calendar.set(Calendar.MONTH, newMonth);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTime();

    }


    public static Date addYearsToDate(final Date date, int noOfYears) {
        Date newDate = new Date(date.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        int year = calendar.get(Calendar.YEAR) + noOfYears;
        calendar.set(Calendar.YEAR, year);
        newDate.setTime(calendar.getTime().getTime());
        return newDate;

    }

    private static Calendar now() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Date today() {
        Calendar calendar = now();

        return calendar.getTime();
    }

    public static Date weeksAgo(int weeks) {
        Calendar calendar = now();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, day - weeks * 7 + 1);

        return calendar.getTime();
    }

    public static Date monthsAgo(int months) {
        Calendar calendar = now();

        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - months);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, day + 1);

        return calendar.getTime();
    }

    public static Date yearsAgo(int years) {
        Calendar calendar = now();

        int year = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, year - years);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, day + 1);

        return calendar.getTime();
    }
}
