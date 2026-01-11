public abstract class Employee extends User {
    private final int personnelNr;

    public Employee(String firstName, String lastName, int personnelNr, String password) {
        super(firstName, lastName, password);
        this.personnelNr = personnelNr;
    }

    @Override
    public String getID() {
        return String.valueOf(this.personnelNr);
    }

    public int getPersonnelNr() {
        return this.personnelNr;
    }
}
