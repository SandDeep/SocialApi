package com.twitter.ibeat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
public class DateTimeUtils {
	public static long get5MinuteSliceStamp(int mins) {
		 Calendar c = new GregorianCalendar();
    	 int minute = c.get(Calendar.MINUTE);
    	 c.set(Calendar.MINUTE, (minute/mins)*mins);
    	 c.set(Calendar.SECOND, 0);
    	 c.set(Calendar.MILLISECOND, 0);
    	 return c.getTimeInMillis();
	}
	
	public static long getMinSliceStamp() 
	{
		 Calendar c = new GregorianCalendar();
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getSecSliceStamp() 
	{
		 Calendar c = new GregorianCalendar();
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	
	public static long getHourSliceStamp() {
		 Calendar c = new GregorianCalendar();
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getNHourSliceStamp(int hour) {
		 Calendar c = new GregorianCalendar();
		 int hours = c.get(Calendar.HOUR);
		 c.set(Calendar.HOUR, (hours/hour)*hour);
		 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getNBackHourSliceStamp(int hour) {
		 Calendar c = new GregorianCalendar();
		 int hours = c.get(Calendar.HOUR) - hour;
		 c.set(Calendar.HOUR, hours);
		 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getHalfHourSliceStamp() {
		Long time = System.currentTimeMillis();
		 Calendar c = new GregorianCalendar();
	   	 c.set(Calendar.MINUTE, 30);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 long halfHourTime = c.getTimeInMillis();
	   	 if(time > halfHourTime){
	   		 c.set(Calendar.MINUTE, 60);
	   	 }
	   	 return c.getTimeInMillis();
	}
	
	public static long getMinSliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getHourSliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getDailySliceStamp() {
		 Calendar c = new GregorianCalendar();
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getDailySliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}

	public static long getWeeklySliceStamp() {
		 Calendar c = new GregorianCalendar();
		 c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getNBackWeeklySliceStamp(int N) {
		 Calendar c = new GregorianCalendar();
		 c.add(Calendar.WEEK_OF_MONTH, -N);
		 c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getWeeklySliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getNBackMonthlySliceStamp(int N) {
		 Calendar c = new GregorianCalendar();
		 c.add(Calendar.MONTH, -N);
		 c.set(Calendar.DAY_OF_MONTH,1);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getMonthlySliceStamp() {
		 Calendar c = new GregorianCalendar();
		 c.set(Calendar.DAY_OF_MONTH,1);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getMonthlySliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 c.set(Calendar.DAY_OF_MONTH,1);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getYearlySliceStamp() {
		 Calendar c = new GregorianCalendar();
		 c.set(Calendar.DAY_OF_YEAR,1);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getYearlySliceStamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 c.set(Calendar.DAY_OF_YEAR,1);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static String getFormattedData(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date d = new Date(ts);
		String s = sdf.format(d);
		return s;
	}
	
	public static String getFormattedData1(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		Date d = new Date(ts);
		String s = sdf.format(d);
		return s;
	}
	
	public static long getDateToEpoch(String date) {
		Date epoch;
		try {
			epoch = new java.text.SimpleDateFormat ("dd/MM/yyyy HH:mm:ss").parse(date);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			return getHourSliceStamp();
		}
		Calendar c = new GregorianCalendar();
		c.setTime(epoch);
		return c.getTimeInMillis();
	}
	
	public static long getDateToEpochOEM(String date) {
		Date epoch;
		try {
			epoch = new java.text.SimpleDateFormat ("MM/dd/yyyy HH:mm:ss").parse(date);
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
			return getHourSliceStamp();
		}
		Calendar c = new GregorianCalendar();
		c.setTime(epoch);
		return c.getTimeInMillis();
	}
	
	public static long adJustTimestamp(long ts) {
		long curTs = System.currentTimeMillis();
		if(curTs - ts <= 300000) {
			return getHourSliceStamp(getHourSliceStamp() - 300000);
		}
		return ts;
	}
	
	public static long adJustInstantTimestamp(long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 int minute = c.get(Calendar.MINUTE);
    	 c.set(Calendar.MINUTE, (minute/5)*5);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}

	public static String getDateYYYYMMDD(long epochTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date d = new Date(epochTime);
		//System.out.println(d);
		String s = sdf.format(d);
		return s;
	}
	
	public static String getDateYYYYMM(long epochTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date d = new Date(epochTime);
		//System.out.println(d);
		String s = sdf.format(d);
		return s;
	}
	
	public static long get7DaysbackDailySliceStamp() {
		 Calendar c = new GregorianCalendar();
		 c.add(Calendar.DATE, -7);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static long getNDaysbackDailySliceStamp(int N) {
		 Calendar c = new GregorianCalendar();
		 c.add(Calendar.DATE, -N);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 return c.getTimeInMillis();
	}
	
	public static String getDateDDMMYYYY(String date)
	{
		String oldFormat = "yyyyMMdd";
	    String newFormat = "ddMMyyyy";

	    SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
	    SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);


	    try {
	        return sdf2.format(sdf1.parse(date));

	    } catch (ParseException e) {
	        return null;
	    }
	}
	
	public static String getDateFormated(long epochTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh.mm aaa");
		Date d = new Date(epochTime);
		String s = sdf.format(d);
		return s;
	}
	
	public static String getDateFormat(long epochTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy hh.mm");
		Date d = new Date(epochTime);
		String s = sdf.format(d);
		return s;
	}
	
	public static String getOEMDateFormat(long epochTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
		Date d = new Date(epochTime);
		String s = sdf.format(d);
		return s;
	}
	
	public static long getBackeDate(int days,long ts) {
		 Calendar c = new GregorianCalendar();
		 c.setTimeInMillis(ts);
		 c.set(Calendar.HOUR_OF_DAY, 0);
	   	 c.set(Calendar.MINUTE, 0);
	   	 c.set(Calendar.SECOND, 0);
	   	 c.set(Calendar.MILLISECOND, 0);
	   	 c.add(Calendar.DAY_OF_YEAR, -days);
	   	 return c.getTimeInMillis();
	}
	
	public static int getDayOfMonth(long ts) {
		 Calendar c = new GregorianCalendar();
		 int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
	   	 return dayOfMonth;
	}
	
	public static int getDayOfWeek(long ts) {
		 Calendar c = new GregorianCalendar();
		 int dayOfMonth = c.get(Calendar.DAY_OF_WEEK);
	//	 System.out.println(c.get(Calendar.DAY_OF_WEEK_IN_MONTH));
	   	 return dayOfMonth;
	}
	
	public static int getHourOfDay(long ts) {
		 Calendar c = new GregorianCalendar();
		 int hour = c.get(Calendar.HOUR_OF_DAY);
	   	 return hour;
	}
	
	public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");

    public static long toLocalTime(long time, TimeZone to) {
        return convertTime(time, utcTZ, to);
    }

    public static long toUTC(long time, TimeZone from) {
        return convertTime(time, from, utcTZ);
    }

    public static long convertTime(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0){
            if (toOffset > 0){
                toOffset = -1*toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset+toOffset)*-1;
        } else {
            if (toOffset <= 0){
                toOffset = -1*Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset)+toOffset);
        }
        return diff;
    }
    public static String getUTCStringDate(Long time,SimpleDateFormat sdf){
    	System.out.println("Date--------");
    	System.out.println(new Date(time));
	        long utcTimeStamp = DateTimeUtils.toUTC(time, new GregorianCalendar().getTimeZone());
	        Calendar utcCal = Calendar.getInstance();
	        utcCal.setTimeInMillis(utcTimeStamp);
	        System.out.println("toUTC: "+sdf.format(utcCal.getTime())+"\n");
	        return sdf.format(utcCal.getTime());
    }
	
	public static void main(String[] args) {
		
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss zzzz");
	        Calendar date1 = new GregorianCalendar();
	        System.out.println(sdf.format(date1.getTime())+"\n");
	        long utcTimeStamp = DateTimeUtils.toUTC(date1.getTimeInMillis(), date1.getTimeZone());
	        Calendar utcCal = Calendar.getInstance();
	        utcCal.setTimeInMillis(utcTimeStamp);
	        System.out.println("toUTC: "+sdf.format(utcCal.getTime())+"\n");

	        System.out.println("---------------------------------------");
	        Calendar date2 = new GregorianCalendar();
	        System.out.println(sdf.format(date2.getTime())+"\n");
	        utcTimeStamp = DateTimeUtils.toUTC(date2.getTimeInMillis(), date2.getTimeZone());
	        utcCal.setTimeInMillis(utcTimeStamp);
	        System.out.println("toUTC: "+sdf.format(utcCal.getTime())+"\n");

	        System.out.println("---------------------------------------");
	        Calendar date3 = new GregorianCalendar();
	        System.out.println(sdf.format(date3.getTime())+"\n");
	        long uTime = DateTimeUtils.toUTC(date3.getTimeInMillis(), date3.getTimeZone());
	        System.out.println("utcTimeStamp: "+uTime+"\n");
	        long lTime = DateTimeUtils.toLocalTime(uTime, TimeZone.getTimeZone("EST"));
	        Calendar locCal = Calendar.getInstance();
	        locCal.setTimeInMillis(lTime);
	        System.out.println("toLocal: "+sdf.format(locCal.getTime())+"\n");

	        System.out.println("---------------------------------------");
	        Calendar date4 = new GregorianCalendar();
	        System.out.println(sdf.format(date4.getTime())+"\n");
	        uTime = DateTimeUtils.toUTC(date4.getTimeInMillis(), date4.getTimeZone());
	        System.out.println("utcTimeStamp: "+uTime+"\n");
	        lTime = DateTimeUtils.toLocalTime(uTime, TimeZone.getTimeZone("EST"));
	        locCal = Calendar.getInstance();
	        locCal.setTimeInMillis(lTime);
	        System.out.println("toLocal: "+sdf.format(locCal.getTime())+"\n");
	}
}
