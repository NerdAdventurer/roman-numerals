/**
 * 
 */
package com.documill.exercise;

/**
 * Exception class for handling malformatted Roman numerals.
 * @author Aki Laukkanen
 */
public class RomanNumeralFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public RomanNumeralFormatException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public RomanNumeralFormatException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public RomanNumeralFormatException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RomanNumeralFormatException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public RomanNumeralFormatException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
