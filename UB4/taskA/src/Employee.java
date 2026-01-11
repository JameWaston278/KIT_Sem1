/**
 * Abstract class representing a staff member (Mailman or Agent).
 * Identified by a numeric personnel ID.
 */
public abstract class Employee extends User {

    private final int personnelId;

    /**
     * Constructs a new Employee.
     *
     * @param firstName   First name.
     * @param lastName    Last name.
     * @param personnelId Unique numeric personnel ID.
     * @param password    Login password.
     */
    public Employee(String firstName, String lastName, int personnelId, String password) {
        super(firstName, lastName, password);
        this.personnelId = personnelId;
    }

    /**
     * Returns the Personnel ID as the unique string identifier.
     * 
     * @return The personnel ID as a String.
     */
    @Override
    public String getID() {
        return String.valueOf(this.personnelId);
    }

    /**
     * Gets the numeric personnel ID.
     * 
     * @return The personnel ID (int).
     */
    public int getPersonnelId() {
        return this.personnelId;
    }
}