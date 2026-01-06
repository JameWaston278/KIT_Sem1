public class Customer extends User {
    private String username;
    private String personalNr;

    public Customer(String firstName, String lastName, String usernamer, String password, String personalNr) {
        super(firstName, lastName, password);

        this.username = usernamer;
        this.personalNr = personalNr;
    }

    @Override
    public String getID() {
        return this.username;
    }
}
