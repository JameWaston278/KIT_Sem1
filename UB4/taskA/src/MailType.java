public enum MailType {
    BRIEF("Brief", 70),
    EINWURF_EINSCHREIBEN("Einwurf-Einschreiben", 120),
    EINSCHREIBEN("Einschreiben", 200),
    PAKET_S("PaketS", 500),
    PAKET_M("PaketM", 600),
    PAKET_L("PaketL", 700);

    private final String type;
    private final int price;

    private MailType(String type, int price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price / 100;
    }

    public static MailType fromString(String input) {
        for (MailType mail : values()) {
            if (mail.type.equals(input)) {
                return mail;
            }
        }
        return null;
    }
}
