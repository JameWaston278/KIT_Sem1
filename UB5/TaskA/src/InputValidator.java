import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class InputValidator {

    // --- REGEX PATTERNS ---

    // <name>: keine Zeilenumbrüche, oder Leerzeichen
    private static final Pattern PATTERN_NAME = Pattern.compile("^\\S+$");

    // <tag>: Buchstaben (A-Z, a-z) und Zahlen
    private static final Pattern PATTERN_TAG = Pattern.compile("^[a-zA-Z0-9]+$");

    // <list>: nur aus Buchstaben (A-Z, a-z)
    private static final Pattern PATTERN_LIST = Pattern.compile("^[a-zA-Z]+$");

    // --- VALIDATION METHODS ---

    public static int parseId(String input) throws SystemException {
        try {
            int id = Integer.parseInt(input);
            if (id < 1)
                throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new SystemException(SystemMessage.INVALID_ID.format());
        }
    }

    public static String validateName(String input) throws SystemException {
        if (!PATTERN_NAME.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_NAME.format());
        }
        return input;
    }

    public static String validateListName(String input) throws SystemException {
        if (!PATTERN_LIST.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_LIST_NAME.format());
        }
        return input;
    }

    public static Priority parsePriority(String input) throws SystemException {
        try {
            return Priority.valueOf(input); // HI, MD, LO
        } catch (IllegalArgumentException e) {
            throw new SystemException(SystemMessage.INVALID_PRIORITY.format());
        }
    }

    public static LocalDate parseDate(String input) throws SystemException {
        // Format: JJJJ-MM-TT (YYYY-MM-DD)
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new SystemException(SystemMessage.INVALID_DEADLINE.format());
        }
    }

    public static String validateTag(String input) throws SystemException {
        if (!PATTERN_TAG.matcher(input).matches()) {
            throw new SystemException(SystemMessage.INVALID_TAG_NAME.format());
        }
        return input;
    }
}