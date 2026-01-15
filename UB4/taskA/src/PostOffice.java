import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The core logic of the Post Office system.
 * This class manages users (Customers, Mailmen, Agents), handles mail
 * processing,
 * and generates reports.
 */
public class PostOffice {

    /**
     * Functional interface to simplify employee creation logic.
     * Used to pass the constructor of a specific Employee type (Mailman/Agent)
     * to the generic addEmployee helper method.
     */
    @FunctionalInterface
    private interface EmployeeFactory {
        /**
         * Creates a new Employee instance.
         *
         * @param firstName   The first name.
         * @param lastName    The last name.
         * @param personnelID The unique numeric personnel ID.
         * @param password    The login password.
         * @return A new instance of an Employee subclass.
         */
        Employee create(String firstName, String lastName, int personnelID, String password);
    }

    // --- FIELDS ---

    /**
     * Stores all registered users mapped by their unique identifier (Username or
     * Personnel Number).
     */
    private final Map<String, User> registeredUsers = new HashMap<>();

    /** The user currently logged into the system. Null if no one is logged in. */
    private User currentUser = null;

    // --- CONSTRUCTOR ---

    /**
     * Default constructor for PostOffice.
     * Initializes the user storage.
     */
    public PostOffice() {
        // Default constructor
    }

    // --- USER MANAGEMENT ---

    /**
     * Registers a new customer in the system.
     *
     * @param firstName The first name of the customer.
     * @param lastName  The last name of the customer.
     * @param userName  The unique username for login (4-9 chars).
     * @param password  The login password (4-9 chars).
     * @param idCard    The 9-digit ID card number.
     * @throws SystemException If validation fails or the user already exists.
     */
    public void addCustomer(String firstName, String lastName, String userName, String password, String idCard)
            throws SystemException {
        // 1. Validate basic info (shared logic for all users)
        validateBasicInfo(firstName, lastName, password);

        // 2. Validate specific customer format
        validateFormat(userName, "[a-zA-Z0-9]{4,9}", SystemMessage.INVALID_USERNAME_FORMAT);
        if (userName.matches("[0-9]+")) {
            throw new SystemException(SystemMessage.USERNAME_NUMERIC_ONLY.format());
        }
        validateFormat(idCard, "[0-9]{9}", SystemMessage.INVALID_ID_CARD_FORMAT);

        // 3. Check uniqueness
        if (registeredUsers.containsKey(userName)) {
            throw new SystemException(SystemMessage.USER_EXISTS.format());
        }

        for (User u : registeredUsers.values()) {
            if (u instanceof Customer existingCustomer) {
                if (existingCustomer.getIdCard().equals(idCard)) {
                    throw new SystemException(SystemMessage.ID_CARD_EXISTS.format());
                }
            }
        }

        // 4. Register
        Customer newCustomer = new Customer(firstName, lastName, userName, password, idCard);
        registeredUsers.put(userName, newCustomer);
    }

    /**
     * Registers a new Mailman.
     *
     * @param firstName   The first name.
     * @param lastName    The last name.
     * @param personnelId The unique personnel number (numeric).
     * @param password    The login password.
     * @throws SystemException If validation fails or ID is occupied.
     */
    public void addMailman(String firstName, String lastName, String personnelId, String password)
            throws SystemException {
        addEmployee(firstName, lastName, personnelId, password,
                (fn, ln, id, pw) -> new Mailman(fn, ln, id, pw));
    }

    /**
     * Registers a new Agent (Call center employee).
     *
     * @param firstName   The first name.
     * @param lastName    The last name.
     * @param personnelId The unique personnel number (numeric).
     * @param password    The login password.
     * @throws SystemException If validation fails or ID is occupied.
     */
    public void addAgent(String firstName, String lastName, String personnelId, String password)
            throws SystemException {
        addEmployee(firstName, lastName, personnelId, password,
                (fn, ln, id, pw) -> new Agent(fn, ln, id, pw));
    }

    /**
     * Resets a customer's PIN (password). Can only be performed by an Agent.
     *
     * @param userName The username of the customer.
     * @param idCard   The ID card number for verification.
     * @param password The new password.
     * @throws SystemException If the current user is not an Agent, or verification
     *                         fails.
     */
    public void resetPin(String userName, String idCard, String password) throws SystemException {
        if (!(currentUser instanceof Agent)) {
            throw new SystemException(SystemMessage.UNAUTHORIZED_RESET.format());
        }

        if (!registeredUsers.containsKey(userName) || !(registeredUsers.get(userName) instanceof Customer)) {
            throw new SystemException(SystemMessage.USER_NOT_FOUND.format(userName));
        }
        Customer customer = (Customer) registeredUsers.get(userName);

        if (!customer.getIdCard().equals(idCard)) {
            throw new SystemException(SystemMessage.ID_CARD_MISMATCH.format());
        }

        validateFormat(password, "[a-zA-Z0-9]{4,9}", SystemMessage.INVALID_RESET_PASSWORD_FORMAT);

        customer.setPassword(password);
    }

    // --- AUTHENTICATION ---

    /**
     * Authenticates a user into the system.
     *
     * @param userName The username or personnel number.
     * @param password The password.
     * @throws SystemException If a user is already logged in or credentials are
     *                         invalid.
     */
    public void authenticate(String userName, String password) throws SystemException {
        if (this.currentUser != null) {
            throw new SystemException(SystemMessage.USER_ALREADY_LOGGED_IN.format());
        }

        User foundUser = registeredUsers.get(userName);
        if (foundUser == null) {
            throw new SystemException(SystemMessage.USER_NOT_FOUND.format(userName));
        }
        if (!foundUser.checkPassword(password)) {
            throw new SystemException(SystemMessage.INCORRECT_PASSWORD.format(password, userName));
        }

        this.currentUser = foundUser;
    }

    /**
     * Logs out the current user.
     *
     * @throws SystemException If no user is currently logged in.
     */
    public void logout() throws SystemException {
        if (this.currentUser == null) {
            throw new SystemException(SystemMessage.NO_USER_LOGGED_IN.format());
        }
        this.currentUser = null;
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return The User object, or null if no one is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // --- MAIL OPERATIONS ---

    /**
     * Sends a mail from one customer to another.
     *
     * @param mailType         The type of mail (e.g., Brief, PaketS).
     * @param receiverUsername The username of the recipient.
     * @param senderUsername   The username of the sender.
     * @throws SystemException If validation fails (e.g., invalid registeredUsers,
     *                         unknown mail
     *                         type).
     */
    public void sendMail(String mailType, String receiverUsername, String senderUsername) throws SystemException {
        MailType type = MailType.fromString(mailType);
        if (type == null) {
            throw new SystemException(SystemMessage.UNKNOWN_MAIL_TYPE.format());
        }

        // Validate Receiver
        if (!registeredUsers.containsKey(receiverUsername)
                || !(registeredUsers.get(receiverUsername) instanceof Customer)) {
            throw new SystemException(SystemMessage.RECEIVER_NOT_CUSTOMER.format());
        }
        Customer receiver = (Customer) registeredUsers.get(receiverUsername);

        // Validate Sender
        if (!registeredUsers.containsKey(senderUsername)
                || !(registeredUsers.get(senderUsername) instanceof Customer)) {
            throw new SystemException(SystemMessage.SENDER_NOT_CUSTOMER.format());
        }
        Customer sender = (Customer) registeredUsers.get(senderUsername);

        receiver.receiveMail(new MailItem(type, sender.getID(), receiver.getID()));
        sender.recordSentMail(type);
    }

    /**
     * Retrieves (fetches) the oldest mail from a customer's mailbox.
     *
     * @param customerUsername The username of the customer.
     * @throws SystemException If the mailbox is empty or user is invalid.
     */
    public void getMail(String customerUsername) throws SystemException {
        if (!registeredUsers.containsKey(customerUsername)
                || !(registeredUsers.get(customerUsername) instanceof Customer)) {
            throw new SystemException(SystemMessage.RECEIVER_NOT_CUSTOMER.format());
        }
        Customer customer = (Customer) registeredUsers.get(customerUsername);

        if (customer.isMailBoxEmpty()) {
            throw new SystemException(SystemMessage.MAILBOX_EMPTY.format());
        }
        customer.removeOldestMail();
    }

    // --- REPORTING ---

    /**
     * Lists all mail currently in the customer's mailbox.
     * Output format: "MailType; Count"
     *
     * @param customerUsername The username of the customer.
     * @throws SystemException If user is invalid.
     */
    public void listMail(String customerUsername) throws SystemException {
        if (!registeredUsers.containsKey(customerUsername)
                || !(registeredUsers.get(customerUsername) instanceof Customer)) {
            throw new SystemException(SystemMessage.RECEIVER_NOT_CUSTOMER.format());
        }
        Customer customer = (Customer) registeredUsers.get(customerUsername);

        if (customer.isMailBoxEmpty()) {
            System.out.println(SystemMessage.OK);
        }

        Map<MailType, Integer> counts = new HashMap<>();
        for (MailItem item : customer.getMailBox()) {
            MailType type = item.getType();
            counts.put(type, counts.getOrDefault(type, 0) + 1);
        }

        List<MailType> sortedTypes = getSortedMailTypes(counts.keySet());

        for (MailType type : sortedTypes) {
            System.out.println(type.getLabel() + ";" + counts.get(type));
        }
    }

    /**
     * Lists the history of sent mail and calculates the total price.
     * Output format: "MailType; Count; TotalPrice"
     *
     * @param customerUsername The username of the customer.
     * @throws SystemException If user is invalid.
     */
    public void listPrice(String customerUsername) throws SystemException {
        if (!registeredUsers.containsKey(customerUsername)
                || !(registeredUsers.get(customerUsername) instanceof Customer)) {
            throw new SystemException(SystemMessage.RECEIVER_NOT_CUSTOMER.format());
        }
        Customer customer = (Customer) registeredUsers.get(customerUsername);

        Map<MailType, Integer> history = customer.getMailHistory();

        if (history.isEmpty()) {
            System.out.println(SystemMessage.OK);
        }

        List<MailType> sortedTypes = getSortedMailTypes(history.keySet());
        for (MailType type : sortedTypes) {
            int count = history.get(type);
            int totalPrice = type.getPrice() * count;

            System.out.println(type.getLabel() + ";" + count + ";"
                    + String.format(java.util.Locale.US, "%.2f", (double) totalPrice / 100));
        }
    }

    // --- PRIVATE HELPERS ---

    /**
     * Generic helper to register any type of Employee (Mailman or Agent).
     *
     * @param firstName   The first name.
     * @param lastName    The last name.
     * @param personnelId The personnel number as a string.
     * @param password    The login password.
     * @param factory     The factory to create the specific Employee instance.
     * @throws SystemException If basic validation fails or ID is taken.
     */
    private void addEmployee(String firstName, String lastName, String personnelId, String password,
            EmployeeFactory factory) throws SystemException {
        validateBasicInfo(firstName, lastName, password);
        validateFormat(personnelId, "[0-9]+", SystemMessage.INVALID_PERSONNEL_ID_FORMAT);

        if (registeredUsers.containsKey(personnelId)) {
            User existingEmployee = registeredUsers.get(personnelId);
            if (existingEmployee instanceof Employee) {
                throw new SystemException(SystemMessage.EMPLOYEE_EXISTS.format());
            } else {
                throw new SystemException(SystemMessage.ID_OCCUPIED.format(personnelId));
            }
        }

        try {
            int personnelID = Integer.parseInt(personnelId);
            if (personnelID <= 0) {
                throw new SystemException(SystemMessage.PERSONNEL_ID_NOT_POSITIVE.format());
            }
            Employee newEmployee = factory.create(firstName, lastName, personnelID, password);
            registeredUsers.put(personnelId, newEmployee);
        } catch (NumberFormatException e) {
            throw new SystemException(SystemMessage.PERSONNEL_ID_TOO_LARGE.format());
        }
    }

    /**
     * Validates common user information (name, password, login status).
     *
     * @param firstName The first name to validate.
     * @param lastName  The last name to validate.
     * @param password  The password to validate.
     * @throws SystemException If a user is logged in or validation fails.
     */
    private void validateBasicInfo(String firstName, String lastName, String password) throws SystemException {
        if (this.currentUser != null) {
            throw new SystemException(SystemMessage.USER_ALREADY_LOGGED_IN.format());
        }
        validateFormat(firstName, "[^;\\n\\r]+", SystemMessage.INVALID_FIRST_NAME);
        validateFormat(lastName, "[^;\\n\\r]+", SystemMessage.INVALID_LAST_NAME);
        validateFormat(password, "[a-zA-Z0-9]{4,9}", SystemMessage.INVALID_PASSWORD_FORMAT);
    }

    /**
     * Validates an input string against a regex pattern.
     *
     * @param input        The input string to check.
     * @param regex        The regular expression pattern.
     * @param errorMessage The message to throw if validation fails.
     * @throws SystemException If input is null or does not match regex.
     */
    private void validateFormat(String input, String regex, SystemMessage message) throws SystemException {
        if (input == null || !input.matches(regex)) {
            throw new SystemException(message.toString());
        }
    }

    /**
     * Helper to sort MailTypes alphabetically based on their label.
     *
     * @param types The set of mail types to sort.
     * @return A list of MailTypes sorted by label.
     */
    private List<MailType> getSortedMailTypes(Set<MailType> types) {
        List<MailType> sortedList = new ArrayList<>(types);
        Collections.sort(sortedList, (t1, t2) -> t1.getLabel().compareTo(t2.getLabel()));
        return sortedList;
    }
}