/**
 * Centralizes all system output messages, error notifications, and validation
 * warnings.
 *
 * @author udqch
 * @version 1.0
 */
public enum SystemMessage {

    // =========================================================================
    // SECTION 1: GENERAL SYSTEM STATUS & COMMAND PARSING
    // =========================================================================

    /** Standard fail message. */
    ERROR_PREFIX("Error, "),

    /** Standard success message. */
    OK("OK"),

    /** Error when the input command is not recognized. */
    UNKNOWN_COMMAND("unknown command."),

    /** Error when a command requires parameters but none were given. */
    NO_PARAMS("this command accepts no parameters."),

    /**
     * Error when arguments are missing (Got 0).
     * Arg: %d (Expected count).
     */
    ARG_COUNT_ZERO("invalid number of arguments. Expected %d but got 0"),

    /**
     * Error when argument count does not match.
     * Args: %d (Expected count), %d (Actual count).
     */
    ARG_COUNT_MISMATCH("invalid number of arguments. Expected %d but got %d"),

    // =========================================================================
    // SECTION 2: INPUT VALIDATION (FORMATTING)
    // =========================================================================

    /** Error: First name contains illegal characters (e.g., semicolon). */
    INVALID_FIRST_NAME("first name contains invalid characters."),

    /** Error: Last name contains illegal characters. */
    INVALID_LAST_NAME("last name contains invalid characters."),

    /** Error: Username length or character set is invalid. */
    INVALID_USERNAME_FORMAT("username must be 4-9 alphanumeric characters."),

    /** Error: Username consists only of numbers (must contain letters). */
    USERNAME_NUMERIC_ONLY("username cannot be purely numeric."),

    /** Error: Password length or character set is invalid. */
    INVALID_PASSWORD_FORMAT("password must be 4-9 alphanumeric characters."),

    /** Error: ID Card is not exactly 9 digits. */
    INVALID_ID_CARD_FORMAT("id card number must be exactly 9 digits."),

    /** Error: New password for reset is invalid. */
    INVALID_RESET_PASSWORD_FORMAT(
            "incorrect format, password must be between 4 and 9 characters and contain no invalid characters."),

    /** Error: Personnel ID contains non-numeric characters. */
    INVALID_PERSONNEL_ID_FORMAT("personnel number must be numeric."),

    /** Error: Personnel ID is negative or zero. */
    PERSONNEL_ID_NOT_POSITIVE("personnel number must be a positive number."),

    /** Error: Personnel ID exceeds integer range. */
    PERSONNEL_ID_TOO_LARGE("personnel number is too large."),

    // =========================================================================
    // SECTION 3: DUPLICATION & EXISTENCE CHECKS
    // =========================================================================

    /** Error: Username is already taken by another customer. */
    USER_EXISTS("customer with this username already exists."),

    /** Error: ID Card number is already linked to another customer. */
    ID_CARD_EXISTS("customer with this ID card number already exists."),

    /** Error: Personnel ID is already registered to another employee. */
    EMPLOYEE_EXISTS("employee with this personnel number already exists."),

    /**
     * Error: Generic identifier collision (used when ID type is mixed).
     * Arg: %s (The occupied ID string).
     */
    ID_OCCUPIED("the identifier %s is occupied."),

    /**
     * Error: User lookup failed during login.
     * Arg: %s (The missing username).
     */
    USER_NOT_FOUND("this user \"%s\" does not exist."),

    /** Error: Targeted customer username not found in database. */
    CUSTOMER_NOT_FOUND("customer with this username does not exist."),

    // =========================================================================
    // SECTION 4: AUTHENTICATION & SECURITY
    // =========================================================================

    /** Error: Attempting to login while a session is active. */
    USER_ALREADY_LOGGED_IN("user already logged in."),

    /** Error: Attempting to logout or act without being logged in. */
    NO_USER_LOGGED_IN("no user is authenticated."),

    /**
     * Error: Password mismatch during login.
     * Args: %s (Input password), %s (Target username).
     */
    INCORRECT_PASSWORD("incorrect password \"%s\" for user \"%s\"."),

    /** Error: User lacks permission for the requested action. */
    UNAUTHORIZED_ROLE("unauthorized role."),

    /** Error: Non-agent user attempted to reset a PIN. */
    UNAUTHORIZED_RESET("unauthorized access, only agents can reset PINs."),

    /** Error: Provided ID Card does not match the user's record. */
    ID_CARD_MISMATCH("ID card number does not match."),

    // =========================================================================
    // SECTION 5: MAIL OPERATIONS
    // =========================================================================

    /** Error: Invalid mail type specified. */
    UNKNOWN_MAIL_TYPE("unknown mail type."),

    /** Error: Recipient exists but is not a Customer (e.g., is an Employee). */
    RECEIVER_NOT_CUSTOMER("receiver is not a registered customer."),

    /** Error: Sender exists but is not a Customer. */
    SENDER_NOT_CUSTOMER("sender is not a registered customer."),

    /** Error: Attempting to fetch mail from an empty mailbox. */
    MAILBOX_EMPTY("mailbox is empty.");

    private final String message;

    SystemMessage(String message) {
        this.message = message;
    }

    /**
     * Formats the message pattern with given arguments.
     *
     * @param args Arguments for placeholders (%d, %s).
     * @return The formatted string.
     */
    public String format(Object... args) {
        return String.format(message, args);
    }

    /**
     * Returns the raw message string.
     */
    @Override
    public String toString() {
        return message;
    }
}