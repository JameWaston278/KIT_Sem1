package utils;

/**
 * The StringConstants class defines a collection of constant string values that
 * are commonly used throughout the application.
 * 
 * @author udqch
 */
public final class StringConstants {

    private StringConstants() {
        // Private constructor to prevent instantiation of this utility class
    }

    // String constants for common symbols and delimiters
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String EMPTY = "";

    // Regular expression patterns for splitting strings
    public static final String REGEX_WHITESPACE = "\\s+";
    public static final String REGEX_COMMA = ",";
}