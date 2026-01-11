public abstract class User {
    private final String firstName;
    private final String lastName;
    private String password;

    public User(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public abstract String getID();

    public String getFirstName() {
        return this.firstName;
    }

    public String getLasttName() {
        return this.lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
