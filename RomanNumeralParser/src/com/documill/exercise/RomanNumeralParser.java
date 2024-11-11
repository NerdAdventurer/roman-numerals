/**
 * 
 */
package com.documill.exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class for converting Roman numerals into integers.
 * @author Aki Laukkanen
 */
public class RomanNumeralParser {

	/**
	 * Minimum length of a Roman numeral that may contain multiple digits in deductive position.
	 */
	private static final int MULTIPLE_DEDUCTIVE_MIN_LENGTH=3;
	
	/**
	 * Minimum length of a Roman numeral that may contain more than three of the same digit.
	 */
	private static final int QUADRUPLE_DIGIT_MIN_LENGTH = 4;

	/**
	 * Minimum length of a Roman numeral that may contain the same digit in deductive (left) and additive (right) position of a larger digit.
	 */
	private static final int DEDUCTIVE_AND_ADDITIVE_MIN_LENGTH = 3;
	
	/**
	 * Minimum length of a Roman numeral that may contain a half measure digit ("V", "L", or "D") in deductive (left) position.
	 */
	private static final int DEDUCTIVE_HALF_MEASURE_MIN_LENGTH=2;
	
	private static enum RomanDigit {
		I('I', 1, false), V('V', 5, true), X('X', 10, false), L('L', 50, true), C('C', 100, false), D('D', 500, true), M('M', 1000, false);
		private char symbol;
		private int integerValue;
		private boolean halfMeasure;

		RomanDigit(char symb, int val, boolean half) {
			setSymbol(symb);
			setIntegerValue(val);
			setHalfMeasure(half);
		}

		public char getSymbol() {
			return symbol;
		}

		public void setSymbol(char symbol) {
			this.symbol = symbol;
		}

		public int getIntegerValue() {
			return integerValue;
		}

		public void setIntegerValue(int integerValue) {
			this.integerValue = integerValue;
		}

		public boolean isHalfMeasure() {
			return halfMeasure;
		}

		public void setHalfMeasure(boolean halfMeasure) {
			this.halfMeasure = halfMeasure;
		}
	}

	/**
	 * Main method for testing. Loops until empty input or unhandled exception.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		boolean quit = false;
		InputStreamReader inRead = new InputStreamReader(System.in);
		BufferedReader inBuf = new BufferedReader(inRead);
		while (!quit) {
			System.out.println("Input a Roman numeral to convert, or enter to quit. ");
			try {
				String input = inBuf.readLine();
				if (input == null || input.isEmpty()) {
					quit = true;
				} else {
					try {
						long decimals = RomanNumeralParser.parse(input);
						System.out
								.println("The decimal value of \"" + input + "\" is " + Long.toString(decimals) + ".");
					} catch (RomanNumeralFormatException ef) {
						System.err.println(ef.getMessage());
					}
				}
			} catch (IOException e) {
				System.err.println("Exception while reading user input.");
				quit = true;
			}
		}
	}

	/**
	 * Parse Roman numeral, check for validity, and calculate its value as a long integer.
	 * @param input a Roman numeral as String
	 * @return Value of the Roman numeral as long integer
	 * @throws RomanNumeralFormatException if input is improperly formatted or contains illegal characters
	 */
	private static long parse(String input) throws RomanNumeralFormatException {
		if (containsIllegalCharacters(input.toUpperCase())) {
			throw new RomanNumeralFormatException("Input string: \"" + input + "\" contains illegal characters.");
		}
		if (containsInvalidConstruct(input.toUpperCase())) {
			throw new RomanNumeralFormatException(
					"Input string: \"" + input + "\" is not a well-formatted Roman numeral.");
		}
		char[] inputArray = input.toUpperCase().toCharArray();
		return calculateSum(inputArray);
	}

	/**
	 * Calculate sum of digits in well-formatted Roman numeral.
	 * @param inputArray Array of characters representing digits of Roman numeral
	 * @return sum of Roman numeral digits as integer, accounting for subtractive notion (e.g. {'I','V'} returns (-1 + 5) = 4)
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static long calculateSum(char[] inputArray) throws RomanNumeralFormatException {
		long sum = 0;
		for (int i = 0; i < inputArray.length; i++) {
			if (i < inputArray.length - 1) {
				// If next digit has larger value, subtract this one from it.
				// e.g. IX = -1 + 10 = 9
				if (isLargerNumeralNext(inputArray[i], inputArray[i+1])) {
					sum-=getNumeralValueFor(inputArray[i]);
				}
				else {
					// Otherwise, add this digit to sum.
					sum+=getNumeralValueFor(inputArray[i]);
				}
			}
			else {
				// Add last digit manually to avoid overflow in precedence check.
				sum+=getNumeralValueFor(inputArray[i]);
			}
		}
		return sum;
	}

	/**
	 * @param current Current character from left to right
	 * @param next Next character from left to right
	 * @return <b>true</b> if and only if next numeral's value is higher than that of the current one (e.g. 'V' after 'I'), otherwise <b>false</b>
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static boolean isLargerNumeralNext(char current, char next) throws RomanNumeralFormatException {
		return(getNumeralValueFor(current) < getNumeralValueFor(next));
	}

	/**
	 * Check input for invalid characters.
	 * @param s Representation of Roman numeral as String
	 * @return <b>true</b> if characters other than [IVXLCDM] present (defined in RomanDigit enum)
	 */
	private static boolean containsIllegalCharacters(String s) {
		String validSymbolRegexp = "[" + RomanDigit.I.getSymbol() + RomanDigit.V.getSymbol() + RomanDigit.X.getSymbol()
				+ RomanDigit.L.getSymbol() + RomanDigit.C.getSymbol() + RomanDigit.D.getSymbol()
				+ RomanDigit.M.getSymbol() + "]";
		String filteredRoman = s.replaceAll(validSymbolRegexp, "");
		return !filteredRoman.isEmpty();
	}

	/**
	 * Check input for invalid constructs like "IIII" (should be "IV"), "VV" (should be "X"), "IVI" (should be "V") and so on. 
	 * @param input A String representing a Roman numeral
	 * @return <b>true</b> if invalid construct(s) identified, otherwise <b>false</b>
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static boolean containsInvalidConstruct(String input) throws RomanNumeralFormatException {
		if(hasTooManyRepeatingDigits(input)) {
			return true;
		}
		if(hasRepeatingHalfMeasure(input)) {
			return true;
		}
		if(hasSameNumeralDeductiveAndAdditive(input)) {
			return true;
		}
		if(hasMultipleNumeralsInSameDeductive(input)) {
			return true;
		}
		if(halfMeasureAsDeductive(input)) {
			return true;
		}
		// TODO Further validity checks here?
		return false;
	}

	/**
	 * Check if a half measure ("V", "L", or "D") found in deductive position (left of larger digit) (e.g. "VX", should just be "V" instead).
	 * @param input Roman numeral to validate
	 * @return <b>true</b> if one of ["V", "L", "D"] is found in deductive position (left of larger digit), otherwise <b>false</b>.
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static boolean halfMeasureAsDeductive(String input) throws RomanNumeralFormatException {
		if(input.length() < DEDUCTIVE_HALF_MEASURE_MIN_LENGTH) {
			return false; // Single digit numeral cannot have deductive digits.
		}
		else {
			char[] inArray = input.toCharArray();
			for(int i = 0; i < inArray.length-1; i++) {
				if(isHalfMeasureDigit(inArray[i])) {
					if(getNumeralValueFor(inArray[i]) < getNumeralValueFor(inArray[i+1])) {
						return true; // Half measure value is before larger digit (deductive position).
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if multiple smaller digits are found on left side (deductive position) of a larger digit. (e.g. "IIV")
	 * @param input Roman numeral to validate
	 * @return <b>true</b> if multiple smaller digits are found on the left side (deductive position) of a larger digit, otherwise <b>false</b>.
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static boolean hasMultipleNumeralsInSameDeductive(String input) throws RomanNumeralFormatException {
		char[] inArray = input.toCharArray();
		if(inArray.length < MULTIPLE_DEDUCTIVE_MIN_LENGTH) {
			return false; // A numeral of less than 3 length cannot have multiple deductive digits.
		}
		else {
			for(int i = 1; i < inArray.length-1; i++) {
				int prevVal=getNumeralValueFor(inArray[i-1]);
				int curVal=getNumeralValueFor(inArray[i]);
				int nextVal = getNumeralValueFor(inArray[i+1]);
				if(prevVal < nextVal && curVal < nextVal) {
					return true; // Two or more smaller numerals before a larger one are not allowed.
				}
			}
		}
		return false;
	}

	/**
	 * Check if same numeral is found in additive and deductive position (e.g. "IVI")
	 * @param input Roman numeral to validate
	 * @return <b>true</b> if a digit is both on left side (deductive position) and right side (additive position) of a larger digit, otherwise <b>false</b>.
	 * @throws RomanNumeralFormatException if invalid character(s) in numeral
	 */
	private static boolean hasSameNumeralDeductiveAndAdditive(String input) throws RomanNumeralFormatException {
		char[] inArray = input.toCharArray();
		if(inArray.length < DEDUCTIVE_AND_ADDITIVE_MIN_LENGTH) {
			return false; // A numeral of less than 3 length cannot have both deductive and additive digits.
		}
		else {
			for(int i = 1; i < inArray.length-1; i++) {
				int prevVal=getNumeralValueFor(inArray[i-1]);
				int curVal=getNumeralValueFor(inArray[i]);
				int nextVal = getNumeralValueFor(inArray[i+1]);
				if(prevVal < curVal && prevVal == nextVal) {
					return true; // Same numeral may not be in additive and deductive position.
				}
			}
		}
		return false;
	}

	/**
	 * Check for repeating "half measure" numerals (should be written as full tens, hundreds, or thousands).
	 * @param input Roman numeral to validate
	 * @return <b>true</b> if input string contains repeating "half measure" numerals ("VV", "LL", or "DD"), otherwise <b>false</b>
	 */
	private static boolean hasRepeatingHalfMeasure(String input) {
		if(input.contains("VV")||input.contains("LL")||input.contains("DD")) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Check if a digit (other than "M") repeats four or more times (should be written in deductive or half measure form, e.g. "IV" not "IIII").
	 * @param input Roman numeral to validate
	 * @return <b>true</b> if a digit other than "M" repeats more than thrice consequently, otherwise <b>false</b>.
	 */
	private static boolean hasTooManyRepeatingDigits(String input) {
		if(input.length() < QUADRUPLE_DIGIT_MIN_LENGTH) {
			return false; // If numeral length is three or less, there cannot be quadruple digits.
		}
		else {
			char[] inArray = input.toCharArray();
			for(int i = 3; i < inArray.length; i++) {
				char prev3 = inArray[i-3];
				char prev2 = inArray[i-2];
				char prev = inArray[i-1];
				char cur = inArray[i];
				if(cur!=RomanDigit.M.getSymbol() && prev3==prev2 && prev2 == prev && prev==cur) {
					return true; // Found 4 concurrent same digits that are not "M".
				}
			}
		}
		return false; // String does not contain more than 3 of the same repeating digit or it is "M".
	}
	
	/**
	 * Get RomanDigit corresponding to character
	 * @param c character to match to Roman digit
	 * @return RomanDigit corresponding to input character
	 * @throws RomanNumeralFormatException if no matching character found
	 */
	private static RomanDigit getRomanDigitFor(char c) throws RomanNumeralFormatException {
		RomanDigit rd = null;
		if (RomanDigit.I.getSymbol() == c) {
			rd = RomanDigit.I;
		} else {
			if (RomanDigit.V.getSymbol() == c) {
				rd = RomanDigit.V;
			} else {
				if (RomanDigit.X.getSymbol() == c) {
					rd = RomanDigit.X;
				} else {
					if (RomanDigit.L.getSymbol() == c) {
						rd = RomanDigit.L;
					} else {
						if (RomanDigit.C.getSymbol() == c) {
							rd = RomanDigit.C;
						} else {
							if (RomanDigit.D.getSymbol() == c) {
								rd = RomanDigit.D;
							} else {
								if (RomanDigit.M.getSymbol() == c) {
									rd = RomanDigit.M;
								}
								else {
									throw new RomanNumeralFormatException("No Roman numeral matches character: "+c);
								}
							}
						}
					}
				}
			}
		}
		return rd;
	}

	/**
	 * @param c Character in character string (as defined in RomanDigit enum)
	 * @return Value of corresponding Roman numeral as integer
	 * @throws RomanNumeralFormatException if character is not valid Roman numeral
	 */
	private static int getNumeralValueFor(char c) throws RomanNumeralFormatException {
		int r = 0;
		RomanDigit rd = getRomanDigitFor(c);
		if(rd!=null) {
			r = rd.getIntegerValue();
		}
		return r;
	}

	/**
	 * @param c Character to check against half measure
	 * @return <b>true</b> if corresponding Roman numeral is a half measure, otherwise <b>false</b>
	 * @throws RomanNumeralFormatException if character is not valid Roman numeral
	 */
	private static boolean isHalfMeasureDigit(char c) throws RomanNumeralFormatException {
		RomanDigit rd = getRomanDigitFor(c);
		if(rd!=null) {
			return rd.isHalfMeasure();
		}
		else {
			return false;
		}
	}
	
}
