package com.ivanceras.db.date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFunctions {
	
	public static String atTimeZone(String column, String timeZone){
		return column + " AT TIME ZONE '"+timeZone+"'";
	}

	public static String getAsString(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getAsString(cal);
	}
	
	public static Date getDate(String dateString) throws ParseException{
		return getDate(dateString, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date getDateZ(String dateString) throws ParseException{
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		return getDate(dateString, format);
	}

	public static Date getDate(String dateString, String format) throws ParseException{
		DateFormat formatter = new SimpleDateFormat(format);
		Date date1;
		date1 = (Date)formatter.parse(dateString);
		return date1;
	}
	
	public static Timestamp getTimestamp(String dateString) throws ParseException{
		Date d1 = getDate(dateString);
		return new Timestamp(d1.getTime());
	}


	public static String getAsString(Calendar cal){
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		int offset = cal.get(Calendar.ZONE_OFFSET);
		String tz = null;
		if(offset > 0){
			tz = "+"+offset/1000/60/60;
		}
		else{
			tz = ""+offset/1000/60/60;//this has already the negative, so don't prefent anything
		}
		String datePart = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds+tz;
		return datePart;
	}

	public static String atTimeZone(Calendar cal, String timeZone){
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		String datePart = "TIMESTAMP WITHOUT TIME ZONE '"+year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds+"'";
		return datePart + " AT TIME ZONE '"+timeZone+"'";
	}


	public static String dateTruncate(String column, String field){
		return "DATE_TRUNC('" + field + "'," + column + ")";
	}

	public static String addInterval(String column, String unit, String field){
		return column+" + INTERVAL '"+unit+" "+field+"'";
	}
	
	public static void main(String args[]){
		Date record = new Date(System.currentTimeMillis());
		System.out.println("now:"+record);
		System.out.println(DateFunctions.getAsString(((Date)record)));
		
		try {
			Date d1 = getDateZ("2012-03-15T20:42:54.023Z");
			System.out.println(d1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
