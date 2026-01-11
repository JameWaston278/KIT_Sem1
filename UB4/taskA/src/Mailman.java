/**
 * Represents a Mailman (Postal worker).
 * Mailmen can send and receive mail on behalf of customers.
 */
public class Mailman extends Employee {
    /**
     * Constructs a new Mailman.
     * 
     * @param firstName   First name.
     * @param lastName    Last name.
     * @param personnelId Numeric personnel ID.
     * @param password    Login password.
     */
    public Mailman(String firstName, String lastName, int personnelId, String password) {
        super(firstName, lastName, personnelId, password);
    }
}