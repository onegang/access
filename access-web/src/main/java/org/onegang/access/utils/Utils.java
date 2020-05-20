package org.onegang.access.utils;

import java.util.Collection;

public class Utils {

	public static void sleepSilently(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
		}
	}
	
	public static <T>T first(Collection<T> values) {
		if(values==null || values.isEmpty())
			return null;
		return values.iterator().next();
	}
}
