/**
 * Represents a Call Center Agent.
 * Agents have the authority to reset customer PINs.
 */
public class Agent extends Employee {
    /**
     * Constructs a new Agent.
     * @param firstName   First name.
     * @param lastName    Last name.
     * @param personnelId Numeric personnel ID.
     * @param password    Login password.
     */
    public Agent(String firstName, String lastName, int personnelId, String password) {
        super(firstName, lastName, personnelId, password);
    }
}