public class MailItem {
    private MailType type;
    private String senderUsername;
    private String receiverUsername;

    public MailItem(MailType type, String senderUsername, String receiverUsername) {
        this.type = type;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }
}