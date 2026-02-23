package model;

public class UnitTemplate {
    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;

    public UnitTemplate(String qualifier, String role, int atk, int def) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
    }

    // --- GETTERS ---
    public String getQualifier() {
        return this.qualifier;
    }

    public String getRole() {
        return this.role;
    }

    public int getAtk() {
        return this.atk;
    }

    public int getDef() {
        return this.def;
    }
}