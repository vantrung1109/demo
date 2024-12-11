package digi.kitplay.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateTimeUtils {

    @SuppressLint("SimpleDateFormat")
    public static Calendar convertStringToTime(String timeString) {
        SimpleDateFormat formatter;

        if (timeString.length() == 5) {
            formatter = new SimpleDateFormat("HH:mm");
        } else if (timeString.length() == 8) {
            formatter = new SimpleDateFormat("HH:mm:ss");
        } else {
            throw new IllegalArgumentException("Invalid time format");
        }
        try {
            Date date = formatter.parse(timeString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Calendar convertToCalendar(int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static String formatTo_mm_ss(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String formatToHHmmss(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String formatTime(String time){
        Calendar calendar = convertStringToTime(time);
        if(calendar.get(Calendar.HOUR) == 0){
            return formatTo_mm_ss(calendar);
        }else {
            return formatToHHmmss(calendar);
        }
    }

    public static String convertHMSToTime(long time){

        long secondsUntilFinished = time / 1000;
        long h = secondsUntilFinished / 3600;
        long m = (secondsUntilFinished % 3600) / 60;
        long s = secondsUntilFinished % 60;
        if(h==0){
            return DateTimeUtils.formatTo_mm_ss(DateTimeUtils.convertToCalendar((int) h, (int) m, (int) s));
        }else {
            return DateTimeUtils.formatToHHmmss(DateTimeUtils.convertToCalendar((int) h, (int) m, (int) s));
        }
    }
    public static Long convertDateTimeToMilliseconds(String dateString){
        if(dateString == null || dateString.isEmpty()){
            return 0L;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateString);
            long milliseconds = date.getTime();
            return milliseconds + 7*60*60*1000;
        } catch (ParseException e) {
            Timber.e(e);
            return 0L;
        }
    }
}
