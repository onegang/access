package org.onegang.access.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class Utils {

	public static void sleepSilently(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
		}
	}
	
	public static String formatDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
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
	
	public static <T extends Comparable<T>> Collection<T> sort(Collection<T> values) {
		if(values==null || values.size()==1)
			return values;
		List<T> sorted = Lists.newArrayList(values);
		Collections.sort(sorted);
		return sorted;
	}
}
