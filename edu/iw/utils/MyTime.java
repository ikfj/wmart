/*
 * Created on 09.04.2005 
 */
package edu.iw.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * A class that provides some Timer utils
 * 
 * @author Bjoern Schnizler, University of Karlsruhe (TH)
 */
public class MyTime {

	/**
	 * Returns the current time
	 * 
	 * @return Time
	 */
	public static long getTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		Date date = calendar.getTime();
		return date.getTime();
	}
}