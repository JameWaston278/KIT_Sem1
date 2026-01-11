/**
 * Enum defining the available types of mail services and their prices (in
 * cents).
 */
public enum MailType {
    BRIEF("Brief", 70),
    EINWURF_EINSCHREIBEN("Einwurf-Einschreiben", 120),
    EINSCHREIBEN("Einschreiben", 200),
    PAKET_S("PaketS", 500),
    PAKET_M("PaketM", 600),
    PAKET_L("PaketL", 700);

    private final String label;
    private final int price;

    /**
     * Private constructor for Enum.
     * 
     * @param label The string representation used in commands.
     * @param price The price in cents.
     */
    MailType(String label, int price) {
        this.label = label;
        this.price = price;
    }

    /**
     * Looks up a MailType by its string representation (case-sensitive).
     *
     * @param input The string label (e.g., "PaketS").
     * @return The matching MailType, or null if not found.
     */
    public static MailType fromString(String input) {
        for (MailType mail : values()) {
            if (mail.label.equals(input)) {
                return mail;
            }
        }
        return null;
    }

    /**
     * Gets the string label of the mail type.
     * 
     * @return The label string.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the price of the mail type.
     * 
     * @return The price in cents.
     */
    public int getPrice() {
        return price;
    }
}