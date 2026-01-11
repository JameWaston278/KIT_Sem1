import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostOffice {

    @FunctionalInterface
    private interface EmployeeFactory {
        Employee create(String firstName, String lastName, int personnelID, String password);
    }

    private final Map<String, User> users = new HashMap<>();
    private User currentUser = null;

    public User getCurrentUser() {
        return currentUser;
    }

    private void validateFormat(String input, String regex, String errorMessage) throws ErrorException {
        if (input == null || !input.matches(regex)) {
            throw new ErrorException(errorMessage);
        }
    }

    private void validateBasicInfo(String firstName, String lastName, String password) throws ErrorException {
        // check login status
        if (this.currentUser != null) {
            throw new ErrorException("operation can not be performed while a user is still logged in.");
        }

        // validate input format
        validateFormat(firstName, "[^;\\n\\r]+", "first name contains invalid characters.");
        validateFormat(lastName, "[^;\\n\\r]+", "last name contains invalid characters.");
        validateFormat(password, "[a-zA-Z0-9]{4,9}",
                "incorrect format, password must be between 4 and 9 characters and contain no invalid characters.");
    }

    public void addCustomer(String firstName, String lastName, String userName, String password, String idCard)
            throws ErrorException {
        // validate basic information of customer
        validateBasicInfo(firstName, lastName, password);

        // validate username and ID Card number
        validateFormat(userName, "[a-zA-Z0-9]{4,9}",
                "incorrect format, user name must be between 4 and 9 characters and contain no invalid characters.");
        validateFormat(idCard, "[0-9]{9}",
                "incorrect format, ID card number must be numeric and exactly 9 characters.");

        // validate unique of username and ID Card number
        if (users.containsKey(userName)) {
            throw new ErrorException("customer with this username already exists.");
        }
        for (User u : users.values()) {
            if (u instanceof Customer existingCustomer) {
                if (existingCustomer.getIdCard().equals(idCard)) {
                    throw new ErrorException("customer with this ID card number already exists.");
                }
            }
        }

        // register new customer
        Customer newCustomer = new Customer(firstName, lastName, userName, password, idCard);
        users.put(userName, newCustomer);
    }

    private void addEmployee(String firstName, String lastName, String personnelNr, String password,
            EmployeeFactory factory)
            throws ErrorException {
        // validate basic information of employee
        validateBasicInfo(firstName, lastName, password);

        // validate input format
        validateFormat(personnelNr, "[0-9]+",
                "incorrect format, ID card number must be numeric.");

        // validate unique of username and personal number
        if (users.containsKey(personnelNr)) {
            User existingEmployee = users.get(personnelNr);
            if (existingEmployee instanceof Employee) {
                throw new ErrorException("employee with this personnel number already exists.");
            } else {
                throw new ErrorException("the identifier " + personnelNr + " is occupied.");
            }
        }

        // register new employee
        try {
            int personnelID = Integer.parseInt(personnelNr);
            Employee newEmployee = factory.create(firstName, lastName, personnelID, password);
            users.put(personnelNr, newEmployee);
        } catch (NumberFormatException e) {
            throw new ErrorException("personnel number is too large.");
        }
    }

    public void addMailman(String firstName, String lastName, String personnelNr, String password)
            throws ErrorException {
        addEmployee(firstName, lastName, personnelNr, password, (fn, ln, id, pw) -> new Mailman(fn, ln, id, pw));
    }

    public void addAgent(String firstName, String lastName, String personnelNr, String password)
            throws ErrorException {
        addEmployee(firstName, lastName, personnelNr, password, (fn, ln, id, pw) -> new Agent(fn, ln, id, pw));
    }

    public void authenticate(String userName, String password) throws ErrorException {
        if (this.currentUser != null) {
            throw new ErrorException(
                    "operation failed, please log out before authenticating with a different account.");
        }

        User foundUser = users.get(userName);
        if (foundUser == null) {
            throw new ErrorException("this user " + userName + " does not exist.");
        }
        if (!foundUser.checkPassword(password)) {
            throw new ErrorException("incorrect password.");
        }

        this.currentUser = foundUser;
    }

    public void logout() throws ErrorException {
        if (this.currentUser == null) {
            throw new ErrorException("no user is authenticated.");
        }

        this.currentUser = null;
    }

    public void sendMail(String mailType, String receiverUsername, String senderUsername) throws ErrorException {
        MailType type = MailType.fromString(mailType);
        if (type == null) {
            throw new ErrorException("unknown mail type.");
        }

        if (!users.containsKey(receiverUsername) || !(users.get(receiverUsername) instanceof Customer)) {
            throw new ErrorException("receiver is not a registered customer.");
        }
        Customer receiver = (Customer) users.get(receiverUsername);

        if (!users.containsKey(senderUsername) || !(users.get(senderUsername) instanceof Customer)) {
            throw new ErrorException("sender is not a registered customer.");
        }
        Customer sender = (Customer) users.get(senderUsername);

        if (senderUsername.equals(receiverUsername)) {
            throw new ErrorException("cannot send mail to oneself.");
        }

        receiver.receiveMail(new MailItem(type, sender.getID(), receiver.getID()));
        sender.addSentItem(type);
    }

    public void getMail(String customerUsername) throws ErrorException {
        if (!users.containsKey(customerUsername) || !(users.get(customerUsername) instanceof Customer)) {
            throw new ErrorException("receiver is not a registered customer.");
        }
        Customer customer = (Customer) users.get(customerUsername);

        if (customer.isMailBoxEmpty()) {
            throw new ErrorException("mailbox is now empty.");
        }
        customer.fetchMail();
    }

    private List<MailType> getSortedMailTypes(Set<MailType> types) {
        List<MailType> sortedList = new ArrayList<>(types);
        Collections.sort(sortedList, (t1, t2) -> t1.getLabel().compareTo(t2.getLabel()));
        return sortedList;
    }

    public void listMail(String customerUsername) throws ErrorException {
        if (!users.containsKey(customerUsername) || !(users.get(customerUsername) instanceof Customer)) {
            throw new ErrorException("receiver is not a registered customer.");
        }
        Customer customer = (Customer) users.get(customerUsername);

        if (customer.isMailBoxEmpty()) {
            System.out.println("OK");
            return;
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

    public void listPrice(String customerUsername) throws ErrorException {
        if (!users.containsKey(customerUsername) || !(users.get(customerUsername) instanceof Customer)) {
            throw new ErrorException("receiver is not a registered customer.");
        }
        Customer customer = (Customer) users.get(customerUsername);

        Map<MailType, Integer> history = customer.getMailHistory();

        if (history.isEmpty()) {
            System.out.println("OK");
            return;
        }

        List<MailType> sortedTypes = getSortedMailTypes(history.keySet());

        for (MailType type : sortedTypes) {
            int count = history.get(type);

            int totalPrice = type.getPrice() * count;

            System.out.println(type.getLabel() + ";" + count + ";"
                    + String.format(java.util.Locale.US, "%.2f", (double) totalPrice / 100));
        }
    }

    public void resetPin(String userName, String idCard, String password) throws ErrorException {
        if (!(currentUser instanceof Agent)) {
            throw new ErrorException(
                    "operation failed for unauthorized role, only agents have authority to reset PIN.");
        }

        if (!users.containsKey(userName) || !(users.get(userName) instanceof Customer)) {
            throw new ErrorException("customer with this username does not exist.");
        }
        Customer customer = (Customer) users.get(userName);

        if (!customer.getIdCard().equals(idCard)) {
            throw new ErrorException("ID card number does not match.");
        }

        validateFormat(password, "[a-zA-Z0-9]{4,9}",
                "incorrect format, password must be between 4 and 9 characters and contain no invalid characters.");

        customer.setPassword(password);
    }
}
