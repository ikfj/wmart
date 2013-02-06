/*
 * Created on Feb 26, 2005
 *
 */
package edu.harvard.econcs.util.csv;

public class CSVException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3257565092366266679L;
	public CSVException(String message) {
		super(message);
	}
	public CSVException(String message, Throwable cause) {
		super(message, cause);
	}
	public CSVException(Throwable cause) {
		super(cause);
	}
}
