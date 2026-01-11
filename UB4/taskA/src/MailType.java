public enum MailType {
    BRIEF("Brief", 70),
    EINWURF_EINSCHREIBEN("Einwurf-Einschreiben", 120),
    EINSCHREIBEN("Einschreiben", 200),
    PAKET_S("PaketS", 500),
    PAKET_M("PaketM", 600),
    PAKET_L("PaketL", 700);

    private final String label;
    private final int price;

    private MailType(String label, int price) {
        this.label = label;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public int getPrice() {
        return price;
    }

    public static MailType fromString(String input) {
        for (MailType mail : values()) {
            if (mail.label.equals(input)) {
                return mail;
            }
        }
        return null;
    }
}
