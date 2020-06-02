package org.onegang.access.utils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class Utils {

	public static void sleepSilently(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
		}
	}
	
	public static boolean isEmpty(String value) {
		return value==null||value.isEmpty();
	}
	
	public static boolean isEmpty(Collection<?> value) {
		return value==null||value.isEmpty();
	}
	
	public static <T>T first(Collection<T> values) {
		if(values==null || values.isEmpty())
			return null;
		return values.iterator().next();
	}
	
	public static Date addDays(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);
		return c.getTime();
	}
}
