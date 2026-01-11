/**
 * Abstract base class representing any user in the Post Office system.
 * Stores common attributes like name and password.
 */
public abstract class User {

    private final String firstName;
    private final String lastName;
    private String password;

    /**
     * Constructs a new User.
     *
     * @param firstName First name of the user.
     * @param lastName  Last name of the user.
     * @param password  Login password.
     */
    public User(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     * Gets the unique identifier for the user.
     * For Customers, this is the Username. For Employees, this is the Personnel ID.
     *
     * @return The unique ID string.
     */
    public abstract String getID();

    /**
     * Verifies if the provided input matches the stored password.
     *
     * @param inputPassword The password to check.
     * @return true if matches, false otherwise.
     */
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // --- GETTERS & SETTERS ---

    /**
     * Retrieves the user's first name.
     * 
     * @return The first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Retrieves the user's last name.
     * 
     * @return The last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Updates the user's password.
     *
     * @param password The new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}