package kit.edu.kastel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for parsing and validating user input.
 * Uses regex patterns to enforce format rules.
 *
 * @author udqch
 */
public final class InputValidator {

    // --- REGEX PATTERNS ---

    // <name>: keine Zeilenumbrüche, oder Leerzeichen
    private static final Pattern PATTERN_NAME = Pattern.compile("^\\S+$");

    // <tag>: Buchstaben (A-Z, a-z) und Zahlen
    private static final Pattern PATTERN_TAG = Pattern.compile("^[a-zA-Z0-9]+$");

    // <list>: nur aus Buchstaben (A-Z, a-z)
    private static final Pattern PATTERN_LIST = Pattern.compile("^[a-zA-Z]+$");

    /** Private constructor. */
    private InputValidator() {
    }
    // --- VALIDATION METHODS ---

    /**
     * Parses and validates a task ID.
     *
     * @param input The string input.
     * @return The parsed integer ID.
     * @throws SystemException If input is not a positive integer.
     */
    public static int parseId(String input) throws SystemException {
        try {
            int id = Integer.parseInt(input);
            if (id < 1) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new SystemException(SystemMessage.INVALID_ID.format());
        }
    }

    /**
     * Validates a task name (no spaces).
     *
     * @param input The input string.
     * @return The validated name.
     * @throws SystemException If format is invalid.
     */
    public static String validateName(String input) throws SystemException {
        if (!PATTERN_NAME.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_NAME.format());
        }
        return input;
    }

    /**
     * Validates a list name (no spaces).
     *
     * @param input The input string.
     * @return The validated name.
     * @throws SystemException If format is invalid.
     */
    public static String validateListName(String input) throws SystemException {
        if (!PATTERN_LIST.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_LIST_NAME.format());
        }
        return input;
    }

    /**
     * Validates a task/ list priority.
     *
     * @param input The input string.
     * @return The validated name.
     * @throws SystemException If format is invalid.
     */
    public static Priority parsePriority(String input) throws SystemException {
        for (Priority p : Priority.values()) {
            if (p.name().equals(input)) {
                return p;
            }
        }
        throw new SystemException(SystemMessage.INVALID_PRIORITY.format());
    }

    /**
     * Validates a task/ list deadline (date).
     *
     * @param input The input string.
     * @return The validated name.
     * @throws SystemException If format is invalid.
     */
    public static LocalDate parseDate(String input) throws SystemException {
        // Format: JJJJ-MM-TT (YYYY-MM-DD)
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new SystemException(SystemMessage.INVALID_DEADLINE.format());
        }
    }

    /**
     * Validates a task/ list tag (no spaces).
     *
     * @param input The input string.
     * @return The validated name.
     * @throws SystemException If format is invalid.
     */
    public static String validateTag(String input) throws SystemException {
        if (!PATTERN_TAG.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_TAG_NAME.format());
        }
        return input;
    }
}