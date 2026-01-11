import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer extends User {
    private final String userName;
    private final String idCard;

    private final List<MailItem> mailBox = new ArrayList<>();
    private final Map<MailType, Integer> mailHistory = new HashMap<>();

    public Customer(String firstName, String lastName, String userName, String password, String idCard) {
        super(firstName, lastName, password);

        this.userName = userName;
        this.idCard = idCard;
    }

    @Override
    public String getID() {
        return this.userName;
    }

    public String getIDcard() {
        return this.idCard;
    }

    public List<MailItem> getMailBox() {
        return this.mailBox;
    }

    public Map<MailType, Integer> getMailHistory() {
        return this.mailHistory;
    }

    public void receiveMail(MailItem item) {
        this.mailBox.add(item);
    }

    public boolean isMailBoxEmpty() {
        return mailBox.isEmpty();
    }

    public void fetchMail() throws ErrorException {
        this.mailBox.remove(0);
    }

    public void addSentItem(MailType type) {
        this.mailHistory.put(type, this.mailHistory.getOrDefault(type, 0) + 1);
    }
}
