/**
 * Represents a physical piece of mail.
 * Contains information about the type, sender, and receiver.
 * This class is immutable.
 */
public class MailItem {

    private final MailType type;
    private final String sender;
    private final String receiver;

    /**
     * Creates a new MailItem.
     *
     * @param type     The category of the mail.
     * @param sender   The identifier (username) of the sender.
     * @param receiver The identifier (username) of the receiver.
     */
    public MailItem(MailType type, String sender, String receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Gets the type of the mail.
     * 
     * @return The MailType enum.
     */
    public MailType getType() {
        return this.type;
    }

    /**
     * Gets the sender's identifier.
     * 
     * @return The sender's username.
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Gets the receiver's identifier.
     * 
     * @return The receiver's username.
     */
    public String getReceiver() {
        return this.receiver;
    }
}