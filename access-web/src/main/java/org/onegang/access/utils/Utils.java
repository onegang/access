package org.onegang.access.utils;

public class Utils {

	public static void sleepSilently(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			
		}
	}
}
