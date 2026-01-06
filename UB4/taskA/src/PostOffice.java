import java.util.HashMap;
import java.util.Map;

public class PostOffice {

    @FunctionalInterface
    private interface EmployeeFactory {
        Employee create(String firstName, String lastName, int personnelID, String password);
    }

    private final Map<String, User> users = new HashMap<>();
    private User currentUser = null;

    private void validateFormat(String input, String regex, String errorMessage) throws ErrorExeption {
        if (input == null || !input.matches(regex)) {
            throw new ErrorExeption(errorMessage);
        }
    }

    private void validateBasicInfo(String firstName, String lastName, String password) throws ErrorExeption {
        // check login status
        if (this.currentUser != null) {
            throw new ErrorExeption("operation can not be performed while a user logged in.");
        }

        // validate input format
        validateFormat(firstName, "[^;\\n\\r]+", "first name contains invalid characters.");
        validateFormat(lastName, "[^;\\n\\r]+", "last name contains invalid characters.");
        validateFormat(password, "[a-zA-Z0-9]{4-9}",
                "incorrect format, password must be between 4 and 9 characters and contain no invalid characters.");

    }

    public void addCustomer(String firstName, String lastName, String userName, String password, String idCard)
            throws ErrorExeption {
        // validate basic information of customer
        validateBasicInfo(firstName, lastName, password);

        // validate username and ID Card number
        validateFormat(userName, "[a-zA-Z0-9]{4-9}",
                "incorrect format, user name must be between 4 and 9 characters and contain no invalid characters.");
        validateFormat(idCard, "[0-9]{9}",
                "incorrect format, ID card number must be numeric and exactly 9 characters.");

        // validate unique of username and ID Card number
        if (users.containsKey(userName)) {
            throw new ErrorExeption("customer with this username already exists.");
        }
        for (User u : users.values()) {
            if (u instanceof Customer existingCustomer) {
                if (existingCustomer.getIDcard().equals(idCard)) {
                    throw new ErrorExeption("customer with this ID card number already exists.");
                }
            }
        }

        // register new customer
        Customer newCustomer = new Customer(firstName, lastName, userName, password, idCard);
        users.put(userName, newCustomer);
    }

    private void addEmployee(String firstName, String lastName, String personnelNr, String password,
            EmployeeFactory factory)
            throws ErrorExeption {
        // validate basic information of employee
        validateBasicInfo(firstName, lastName, password);

        // validate input format
        validateFormat(personnelNr, "[0-9]+",
                "incorrect format, ID card number must be numeric.");

        // validate unique of username and personal number
        if (users.containsKey(personnelNr)) {
            User existingEmployee = users.get(personnelNr);
            if (existingEmployee instanceof Employee) {
                throw new ErrorExeption("employee with this personnel number already exists.");
            } else {
                throw new ErrorExeption("the identifier " + personnelNr + " is occupied.");
            }
        }

        // register new employee
        int personnelID = Integer.parseInt(personnelNr);
        Employee newEmployee = factory.create(firstName, lastName, personnelID, password);
        users.put(personnelNr, newEmployee);
    }

    public void addMailman(String firstName, String lastName, String personnelNr, String password)
            throws ErrorExeption {
        addEmployee(firstName, lastName, personnelNr, password, (fn, ln, id, pw) -> new Mailman(fn, ln, id, pw));
    }

    public void addAgent(String firstName, String lastName, String personnelNr, String password)
            throws ErrorExeption {
        addEmployee(firstName, lastName, personnelNr, password, (fn, ln, id, pw) -> new Agent(fn, ln, id, pw));
    }

    public void authenticate(String userName, String password) throws ErrorExeption {
        if (this.currentUser != null) {
            throw new ErrorExeption(
                    "user is already logged in, please log out first if you want to log in with anther account.");
        }

        User foundUser = users.get(userName);
        if (foundUser == null) {
            throw new ErrorExeption("this user " + userName + " does not exist.");
        }
        if (!foundUser.checkPassword(password)) {
            throw new ErrorExeption("incorrect password.");
        }

        this.currentUser = foundUser;
    }

    public void logout() throws ErrorExeption {
        if (this.currentUser == null) {
            throw new ErrorExeption("no user is authenticated.");
        }

        this.currentUser = null;
    }
}
