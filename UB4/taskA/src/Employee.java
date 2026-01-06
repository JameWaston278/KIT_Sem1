public abstract class Employee extends User {
    private int personalNr;

    public Employee(String firstName, String lastName, int personalNr, String password) {
        super(firstName, lastName, password);
        this.personalNr = personalNr;
    }

    @Override
    public String getID() {
        return String.valueOf(this.personalNr);
    }
}
