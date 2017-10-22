/** A lexical analyzer system for simple arithmetic expression.
 *  @author  Luigi Nunez
 *  @version Sep 20, 2015
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;

public class LexicalAnalyzer {
	// ------------------------------------------
	// Variables
	private static int    charClass;
	private static char[] lexeme = new char[98];
	private static char   nextChar;
	private static int    lexLen;
	private static int	  token;
	private static int    nextToken;
	
	// Character classes
	private static final int LETTER = 0;
	private static final int DIGIT  = 1;
	private static final int UNKNOWN = 99;
	private static final int EOF = -1;
	
	// Token codes
	private static final int INT_LAT = 	   10;
	private static final int IDENT = 	   11;
	private static final int ASSIGN_OP =   20;
	private static final int ADD_OP =      21;
	private static final int SUB_OP =      22;
	private static final int MULT_OP = 	   23;
	private static final int DIV_OP = 	   24;
	private static final int LEFT_PAREN =  25;
	private static final int RIGHT_PAREN = 26;
	
	// Variables for file testing
	private static final String file_name = "expression.txt";
	private static FileInputStream file_in = null;
	
	// ------------------------------------------
	/** Adds nextChar to lexeme */
	public static void addChar() {
		try {
			if (lexLen <= 98) {
				lexeme[lexLen++] = nextChar;
				lexeme[lexLen]	 = 0;
			} else {
				System.out.println("Error - lexeme is too long");
			}
		} catch (NullPointerException e) {
			System.out.println("Error - NullPointer found");
			e.printStackTrace();
			System.exit(0);
		}
	} // end addChar
	
	// ------------------------------------------
	/** Gets the next character of input and determine
	 *  its character class */
	public static void getChar() {
		try {
			if ( (nextChar = (char)file_in.read()) != EOF) {
				if 		(isalpha(nextChar)) charClass = LETTER;
				else if (isdigit(nextChar)) charClass = DIGIT;
				else 						charClass = UNKNOWN;
			} else {
				charClass = EOF;
			}
		} catch (IOException e) {
			System.out.println("Error - I/O issue getting next character");
			e.printStackTrace();
			System.exit(0);
		}
	} // end getChar
	
	// ------------------------------------------
	/** Checks if ch is an alphabetic letter
	 *  @param ch the character to check
	 *  @return true ch is an alphabetic letter, false otherwise
	 */
	private static boolean isalpha(char ch) {
		return (ch >= 65 && ch <= 90) ||
			   (ch >= 97 && ch <= 122);
	} //end isalpha
	
	// ------------------------------------------
	/** Checks if ch is a decimal digit character
	 *  @param ch the character to check
	 *  @return true if ch is an decimal digit, false otherwise
	 */
	private static boolean isdigit(char ch) {
		return (ch >= 48 && ch <= 57);
	} //end isdigit
		
	// ------------------------------------------
	/** Calls getChar until it return a non-whitespace character */
	public static void getNonBlank() {
		while (Character.isWhitespace(nextChar)) getChar();
	} //end getNonBlank
	
	// ------------------------------------------
	/** A simple lexical analyzer for arithmetic expression
	 *  @return the next token */
	public static int lex() {
		lexLen = 0;
		getNonBlank();
		switch (charClass) {
			// Parse Identifiers
			case LETTER:
				addChar();
				getChar();
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = IDENT;
				break;
			// Parse integer literals
			case DIGIT:
				addChar();
				getChar();
				while (charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = INT_LAT;
				break;
			// Parenthesis and operations
			case UNKNOWN:
				lookup(nextChar);
				getChar();
				break;
			// EOFq
			case EOF:
				nextToken = EOF;
				lexeme[0] = 'E';
				lexeme[1] = 'O';
				lexeme[2] = 'F';
				lexeme[3] = 0;
				break;
		} //end switch
		System.out.printf("Next token is %d, Next lexeme is %s\n",
				nextToken, lexemeToString());
		return nextToken;
	} // end lex
	
	// ------------------------------------------
	/** Converts lexeme array to a string
	 *  @return a string representation of lexeme
	 */
	private static String lexemeToString() {
		String str = "[";
		int i = 0;
		boolean emptyFound = false;
		
		try { // NullPointerException thrown if lexeme full
			while(!emptyFound) {
				str += String.valueOf(lexeme[i]);
				if(lexeme[++i] == 0){ emptyFound = true; }
				else 				{ str += ", ";		 }
			} return str + "]";
		} catch(NullPointerException e) {
			return str + "]";
		}
	}
	// ------------------------------------------
	/** Looks up what the non-decimal number,
	 *  non-alphabetic letter character is
	 *  @param ch the character to look up
	 *  @return the token code of ch
	 */
	private static int lookup(char ch) {
		switch (ch) {
			case '(':
				addChar();
				nextToken = LEFT_PAREN;
				break;
			case ')':
				addChar();
				nextToken = RIGHT_PAREN;
				break;
			case '+':
				addChar();
				nextToken = ADD_OP;
				break;
			case '-':
				addChar();
				nextToken = SUB_OP;
				break;
			case '*':
				addChar();
				nextToken = MULT_OP;
				break;
			case '/':
				addChar();
				nextToken = DIV_OP;
				break;
			default:
				addChar();
				nextToken = EOF;
				break;
		}
		return nextToken;
	} // end lookup
	
	// ------------------------------------------
	/** Main method to begin analyzing text file */
	public static void main(String[] args) {
		
		// open the input data file
		try {
			File file = new File(file_name);
		    file_in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Error - File not found");
			e.printStackTrace();
			System.exit(0);
		}
		
		// process the file's contents
		getChar();
		do {
			lex();
		} while (nextToken != EOF);
		
		// close file
	    try {
			file_in.close();
		} catch (IOException e) {
			System.out.println("Error - I/O issue closing file");
			e.printStackTrace();
			System.exit(0);
		}
	} // end main
	
} // end LexicalAnalyzer class
