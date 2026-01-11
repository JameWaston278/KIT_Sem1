import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Customer of the Post Office.
 * Customers have a mailbox for incoming mail and a history of sent mail.
 */
public class Customer extends User {

    private final String username;
    private final String idCard;

    private final List<MailItem> mailBox = new ArrayList<>();
    private final Map<MailType, Integer> mailHistory = new HashMap<>();

    /**
     * Constructs a new Customer.
     *
     * @param firstName First name.
     * @param lastName  Last name.
     * @param username  Unique username.
     * @param password  Login password.
     * @param idCard    9-digit ID card number.
     */
    public Customer(String firstName, String lastName, String username, String password, String idCard) {
        super(firstName, lastName, password);
        this.username = username;
        this.idCard = idCard;
    }

    /**
     * Returns the username as the unique identifier.
     * 
     * @return The username.
     */
    @Override
    public String getID() {
        return this.username;
    }

    // --- BUSINESS LOGIC ---

    /**
     * Adds an incoming mail item to the mailbox.
     *
     * @param item The mail item to receive.
     */
    public void receiveMail(MailItem item) {
        this.mailBox.add(item);
    }

    /**
     * Checks if the mailbox has no items.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isMailBoxEmpty() {
        return mailBox.isEmpty();
    }

    /**
     * Removes and returns the oldest mail from the mailbox (FIFO strategy).
     *
     * @throws ErrorException If the mailbox is already empty.
     */
    public void removeOldestMail() throws ErrorException {
        if (isMailBoxEmpty()) {
            throw new ErrorException("mailbox is empty.");
        }
        this.mailBox.remove(0);
    }

    /**
     * Records a sent mail item in the history for billing purposes.
     * Increments the count for the specific MailType.
     *
     * @param type The type of mail sent.
     */
    public void recordSentMail(MailType type) {
        this.mailHistory.put(type, this.mailHistory.getOrDefault(type, 0) + 1);
    }

    // --- GETTERS ---

    /**
     * Gets the ID Card number.
     * 
     * @return The 9-digit ID card string.
     */
    public String getIdCard() {
        return this.idCard;
    }

    /**
     * Gets the list of current mails in the mailbox.
     * 
     * @return The list of MailItems.
     */
    public List<MailItem> getMailBox() {
        return this.mailBox;
    }

    /**
     * Gets the history of sent mails.
     * 
     * @return A map of MailType to count (frequency).
     */
    public Map<MailType, Integer> getMailHistory() {
        return this.mailHistory;
    }
}