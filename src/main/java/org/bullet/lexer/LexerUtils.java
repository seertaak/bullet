package org.bullet.lexer;

/*
 * Copyright 2003-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.math.BigInteger;
import java.math.BigDecimal;

/**
 *  Helper class for processing Groovy numeric literals.
 *
 *  @author Brian Larson
 *  @author <a href="mailto:cpoirier@dreaming.org">Chris Poirier</a>
 *
 *  @version $Id$
 */
public class LexerUtils {
	//---------------------------------------------------------------------------
	// LEXING SUPPORT
	
	/**
	 *  Returns true if the specified character is a base-10 digit.
	 */
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 *  Returns true if the specific character is a base-8 digit.
	 */
	public static boolean isOctalDigit(char c) {
		return c >= '0' && c <= '7';
	}

	/**
	 *  Returns true if the specified character is a base-16 digit.
	 */
	public static boolean isHexDigit(char c) {
		return isDigit(c) || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	}

	/**
	 *  Returns true if the specified character is a valid type specifier
	 *  for a numeric value.
	 */
	public static boolean isNumericTypeSpecifier( char c, boolean isDecimal ) {
		if( isDecimal ) {
			switch( c ) {
			case 'G':
			case 'g':
			case 'D':
			case 'd':
			case 'F':
			case 'f':
				return true;
			}
		} else {
			switch( c ) {
			case 'G':
			case 'g':
			case 'I':
			case 'i':
			case 'L':
			case 'l':
				return true;
			}
		}
		return false;
	}
	
	// PARSING SUPPORT
	private static final BigInteger MAX_LONG    = BigInteger.valueOf(Long.MAX_VALUE);
	private static final BigInteger MIN_LONG    = BigInteger.valueOf(Long.MIN_VALUE);

	private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	private static final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);

	private static final BigDecimal MAX_DOUBLE  = new BigDecimal(String.valueOf(Double.MAX_VALUE));
	private static final BigDecimal MIN_DOUBLE  = MAX_DOUBLE.negate();

	private static final BigDecimal MAX_FLOAT   = new BigDecimal(String.valueOf(Float.MAX_VALUE));
	private static final BigDecimal MIN_FLOAT   = MAX_FLOAT.negate();

	/**
	 *  Builds a Number from the given integer descriptor.  Creates the narrowest
	 *  type possible, or a specific type, if specified.
	 *
	 *  @param  text literal text to parse
	 *  @return instantiated Number object
	 *  @throws NumberFormatException if the number does not fit within the type
	 *          requested by the type specifier suffix (invalid numbers don't make
	 *          it here)
	 */
	public static Number parseInteger( String text ) {
		// remove place holder underscore before starting
		text = text.replace("_", "");

		char c = ' ';
		int length = text.length();

		// Strip off the sign, if present
		boolean negative = false;
		if( (c = text.charAt(0)) == '-' || c == '+' ) {
			negative = (c == '-');
			text = text.substring( 1, length );
			length -= 1;
		}
		
		// Determine radix (default is 10).
		int radix = 10;
		if( text.charAt(0) == '0' && length > 1 ) {
			c = text.charAt(1);
			if( c == 'X' || c == 'x' ) {
				radix = 16;
				text = text.substring( 2, length);
				length -= 2;
			} else if ( c == 'B' || c == 'b' ) { 
				radix = 2;
				text = text.substring(2, length);
				length -= 2;
			} else {
				radix = 8;
			}
		}

		// Strip off any type specifier and convert it to lower
		// case, if present.
		char type = 'x';  // pick best fit
		if( isNumericTypeSpecifier(text.charAt(length-1), false) ) {
			type = Character.toLowerCase( text.charAt(length-1) );
			text = text.substring( 0, length-1);

			length -= 1;
		}

		// Add the sign back, if necessary
		if( negative )
		{
			text = "-" + text;
		}

		// Build the specified type or, if no type was specified, the
		// smallest type in which the number will fit.
		switch (type)
		{
		case 'i':
			return Integer.valueOf( Integer.parseInt(text, radix) );
		case 'l':
			return new Long( Long.parseLong(text, radix) );
		case 'g':
			return new BigInteger( text, radix );
		default:
			// If not specified, we will return the narrowest possible
			// of Integer, Long, and BigInteger.
			BigInteger value = new BigInteger( text, radix );

			if( value.compareTo(MAX_INTEGER) <= 0 && value.compareTo(MIN_INTEGER) >= 0 )
				return Integer.valueOf(value.intValue());
			else if( value.compareTo(MAX_LONG) <= 0 && value.compareTo(MIN_LONG) >= 0 )
				return new Long(value.longValue());

			return value;
		}
	}
	
	public final static boolean isNumberTerminator(char c) {
		if (c == ' ' || c == ')' || c =='(' || c == ';' || c == ':' ||
				c == '$' || c == '|' || c == '.')
			return true;
		
		return false;
	}
	
	public static String matchInteger(String in) {
		int end = 0;
		char c = ' ';

		if ((c = in.charAt(0)) == '-' || c == '+')
			end++;
		
		// Determine radix (default is 10).
		int radix = 10;
		if( in.charAt(end) == '0' && in.length() > 1 ) {
			c = in.charAt(end+1);
			if( c == 'X' || c == 'x' ) {
				radix = 16;
				end += 2;
			} else if ( c == 'B' || c == 'b' ) { 
				radix = 2;
				end += 2;
			} else {
				radix = 8;
			}
		}
		
		int begin = end;
		
		for (int i = begin; i < in.length(); i++) {
			char ch = in.charAt(i);
			int dch0 = ch - '0';
			int dcha = ch - 'a';
			int dchA = ch - 'A';
			
			if (radix == 2 && dch0 >= 0 && dch0 < 2) {
				end++;
			} else if (radix == 8 && dch0 >= 0 && dch0 < 8) {
				end++;
			} else if (radix == 10 && dch0 >= 0 && dch0 < 10) {
				end++;
			} else if (radix == 16 && 
					((dcha >= 0 && dcha < 6) ||
				     (dchA >= 0 && dchA < 6))) 
			{
				end++;
			} else if (ch == ',') {
				end++;
			} else {
				break;
			}
		}
		
		if (end == begin)
			return null;

		if (isNumericTypeSpecifier(in.charAt(end), false))
			end++;
		
		if (isNumberTerminator(in.charAt(end)))
			return in.substring(0, end);
		
		return null;
	}

	/**
	 *  Builds a Number from the given decimal descriptor.  Uses BigDecimal,
	 *  unless, Double or Float is requested.
	 *
	 *  @param  text literal text to parse
	 *  @return instantiated Number object
	 *  @throws NumberFormatException if the number does not fit within the type
	 *          requested by the type specifier suffix (invalid numbers don't make
	 *          it here)
	 */
	public static Number parseDecimal(String text) {
		text = text.replace("_", "");
		int length = text.length();

		// Strip off any type specifier and convert it to lower
		// case, if present.

		char type = 'x';
		if (isNumericTypeSpecifier(text.charAt(length-1), true)) {
			type = Character.toLowerCase( text.charAt(length-1) );
			text = text.substring( 0, length-1 );

			length -= 1;
		}

		// Build the specified type or default to BigDecimal
		BigDecimal value = new BigDecimal( text );
		switch (type) {
		case 'f':
			if (value.compareTo(MAX_FLOAT) <= 0 && value.compareTo(MIN_FLOAT) >= 0)
				return new Float( text );
			throw new NumberFormatException( "out of range" );
		case 'd':
			if (value.compareTo(MAX_DOUBLE) <= 0 && value.compareTo(MIN_DOUBLE) >= 0)
				return new Double( text );
			throw new NumberFormatException( "out of range" );
		case 'g':
		default:
			return value;
		}
	}


	public static String matchDecimal(String in) {
		int end = 0;
		char c = ' ';

		if ((c = in.charAt(0)) == '-' || c == '+')
			end++;
		
		boolean point = false;
		boolean exp = false;
		
		int begin = end;
		
		for (int i = end; i < in.length(); i++) {
			char ch = in.charAt(i);
			int dch0 = ch - '0';
			
			if (dch0 >= 0 && dch0 < 10) {
				end++;
			} else if (!point && !exp && ch == '.') {
				point = true;
				end++;
			} else if (!exp && (ch == 'e' || ch == 'E')) {
				exp = true;
				end++;
			} else {
				break;
			}
		}
		
		if (end == begin)
			return null;
		
		boolean typeSpec = isNumericTypeSpecifier(in.charAt(end), true);
		if (typeSpec)
			end++;
		
		if (!point && !typeSpec && !point && !exp)
			return null;
		
		c = in.charAt(end);
		if (isNumberTerminator(c))
			return in.substring(0, end);
		
		return null;
	}

}