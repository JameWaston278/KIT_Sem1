import java.util.HashMap;
import java.util.Map;

public class PostOffice {
    private Map<String, User> users = new HashMap<>();
    private User currentUser = null;

    private void validateFormat(String input, String regex, String errorMessage) throws ErrorExeption {
        if (input == null || !input.matches(regex)) {
            throw new ErrorExeption(errorMessage);
        }
    }

    public void checkUniqeIdentifier(String input) throws ErrorExeption {
        if (users.containsKey(input)) {
            throw new ErrorExeption("user with this identifier already exists.");
        }
    }

    public void addCustomer(String firstName, String lastName, String userName, String password, String idCard)
            throws ErrorExeption {
        // check login status
        if (this.currentUser != null) {
            throw new ErrorExeption("operation can not be performed while a user logged in.");
        }

        // validate input format
        validateFormat(firstName, "[^;\\n\\r]+", "first name contains invalid characters.");
        validateFormat(lastName, "[^;\\n\\r]+", "last name contains invalid characters.");
        validateFormat(userName, "[a-zA-Z0-9]{4-9}",
                "incorrect format, user name must be between 4 and 9 characters and contain no invalid characters.");
        validateFormat(password, "[a-zA-Z0-9]{4-9}",
                "incorrect format, password must be between 4 and 9 characters and contain no invalid characters.");
        validateFormat(idCard, "[0-9]{9}",
                "incorrect format, ID card number must be numeric and exactly 9 characters.");

        // validate unique of username and ID Card number
        checkUniqeIdentifier(userName);
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

    public void addEmployee(String role, String firstName, String lastName, String personnelNr, String password)
            throws ErrorExeption {
        // check login status
        if (this.currentUser != null) {
            throw new ErrorExeption("operation can not be performed while a user logged in.");
        }

        // validate input format
        validateFormat(firstName, "[^;\\n\\r]+", "first name contains invalid characters.");
        validateFormat(lastName, "[^;\\n\\r]+", "last name contains invalid characters.");
        validateFormat(personnelNr, "[0-9]+",
                "incorrect format, ID card number must be numeric.");
        validateFormat(password, "[a-zA-Z0-9]{4-9}",
                "incorrect format, password must be between 4 and 9 characters and contain no invalid characters.");

        // validate unique of username and personal number
        checkUniqeIdentifier(personnelNr);

        // register new employee
        if (role.equals("Mailman")){
            
        }
    }
}
