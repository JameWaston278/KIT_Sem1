public class MailItem {
    private final MailType type;
    private final String sender;
    private final String receiver;

    public MailItem(MailType type, String sender, String receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public MailType getType() {
        return this.type;
    }

    public String getSender() {
        return this.sender;
    }

    public String getReceiver() {
        return this.receiver;
    }
}