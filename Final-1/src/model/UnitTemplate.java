package model;

/**
 * The UnitTemplate class represents a template for creating units in the game.
 * It contains information about the unit's qualifier, role, attack value, and
 * defense value. This class is used to define the characteristics of different
 * types of units that can be placed on the board.
 * 
 * @author udqch
 */
public class UnitTemplate {
    private final String qualifier;
    private final String role;
    private final int atk;
    private final int def;

    /**
     * Constructs a new UnitTemplate with the specified attributes.
     * 
     * @param qualifier The qualifier of the unit (e.g., "Elite", "Regular").
     * @param role      The role of the unit (e.g., "Soldier", "Archer").
     * @param atk       The attack value of the unit.
     * @param def       The defense value of the unit.
     */
    public UnitTemplate(String qualifier, String role, int atk, int def) {
        this.qualifier = qualifier;
        this.role = role;
        this.atk = atk;
        this.def = def;
    }

    // --- GETTERS ---

    /**
     * Returns the qualifier of the unit.
     * 
     * @return The qualifier of the unit.
     */
    public String getQualifier() {
        return this.qualifier;
    }

    /**
     * Returns the role of the unit.
     * 
     * @return The role of the unit.
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Returns the attack value of the unit.
     * 
     * @return The attack value of the unit.
     */
    public int getAtk() {
        return this.atk;
    }

    /**
     * Returns the defense value of the unit.
     * 
     * @return The defense value of the unit.
     */
    public int getDef() {
        return this.def;
    }
}