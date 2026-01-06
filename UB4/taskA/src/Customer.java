public class Customer extends User {
    private String userName;
    private String idCard;

    public Customer(String firstName, String lastName, String userName, String password, String idCard) {
        super(firstName, lastName, password);

        this.userName = userName;
        this.idCard = idCard;
    }

    @Override
    public String getID() {
        return this.userName;
    }

    public String getIDcard() {
        return this.idCard;
    }
}
