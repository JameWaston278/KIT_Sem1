public class MailItem {
    private MailType type;
    private String senderUsername;

    public MailItem(MailType type, String senderUsername) {
        this.type = type;
        this.senderUsername = senderUsername;
    }
}