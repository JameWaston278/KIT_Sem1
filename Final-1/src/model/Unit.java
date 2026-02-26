package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.DisplayFormat;
import utils.StringConstants;

/**
 * The Unit class represents a game piece in the board game. Each unit has a
 * qualifier, role, attack and defense values, and an owner (team). Units can be
 * hidden or revealed, can block attacks, and can be eliminated from the game.
 * 
 * @author udqch
 */
public class Unit {
    private String qualifier;
    private final String role;
    private final int atk;
    private final int def;
    private final Team owner;

    private Position position; // The current position of the unit on the board (e.g., "A1", "B2")
    private boolean isHidden;
    private boolean isBlocking;
    private boolean hasMoved;

    /**
     * Constructor for the Unit class, which initializes a unit based on a given
     * template.
     * 
     * @param template The UnitTemplate containing the base stats and properties for
     *                 this unit
     * @param owner    The team that owns this unit
     */
    public Unit(UnitTemplate template, Team owner) {
        this.qualifier = template.getQualifier();
        this.role = template.getRole();
        this.atk = template.getAtk();
        this.def = template.getDef();
        this.owner = owner;

        this.position = null; // Position will be set when the unit is placed on the board
        this.isHidden = true;
        this.isBlocking = false;
        this.hasMoved = false;
    }

    // --- LOGIC METHODS ---

    /**
     * Determines if this unit is a King. By default, units are not Kings, but this
     * method can be overridden in subclasses to identify specific units as Kings.
     * 
     * @return true if this unit is a King, false otherwise
     */
    public boolean isKing() {
        return false;
    }

    /**
     * Blocks an incoming attack. This method sets the unit's blocking status to
     * true and marks it as having moved for the turn. If the unit has already
     * moved, it throws a GameLogicException.
     * 
     * @throws GameLogicException if the unit has already moved this turn and cannot
     *                            block again
     */
    public void block() throws GameLogicException {
        if (this.hasMoved) {
            throw new GameLogicException(ErrorMessage.UNIT_ALREADY_MOVED.format(this.getName()));
        }
        this.isBlocking = true;
        this.hasMoved = true;
    }

    /**
     * Returns a string representation of the unit's information, formatted
     * according to the display template. If the unit is hidden, all values are
     * replaced with a placeholder.
     * 
     * @return a formatted string representation of the unit's information
     */
    public String getInfo() {
        String hiddenSymbol = DisplayFormat.HIDDEN_SYMBOL.getTemplate();
        String displayName = (this.isHidden) ? hiddenSymbol : this.getName();
        String displayOwner = (this.isHidden) ? hiddenSymbol : this.owner.getName();
        String displayAtk = (this.isHidden) ? hiddenSymbol : String.valueOf(this.atk);
        String displayDef = (this.isHidden) ? hiddenSymbol : String.valueOf(this.def);
        return DisplayFormat.UNIT_INFO.format(displayName, displayOwner, displayAtk, displayDef);
    }

    // --- GETTERS & SETTERS ---
    /**
     * Getter for the unit's attack value.
     * 
     * @return the attack value of the unit
     */
    public int getAtk() {
        return this.atk;
    }

    /**
     * Getter for the unit's defense value.
     * 
     * @return the defense value of the unit
     */
    public int getDef() {
        return this.def;
    }

    /**
     * Getter for the unit's qualifier, which is a descriptor that can be used to
     * differentiate units of the same role (e.g., "Red Farmer" vs. "Blue Farmer").
     * 
     * @return the qualifier of the unit
     */
    public String getQualifier() {
        return this.qualifier;
    }

    /**
     * Sets the unit's qualifier to a new value.
     * 
     * @param newQualifier the new qualifier to assign to the unit
     */
    public void setQualifier(String newQualifier) {
        this.qualifier = newQualifier;
    }

    /**
     * Getter for the unit's role, which defines the type of unit (e.g., "Farmer",
     * "Knight", etc.). The role is determined by the unit's template and is
     * immutable after creation.
     * 
     * @return the role of the unit
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Getter for the unit's name, which is a combination of its qualifier and role
     * (e.g., "Red Farmer"). The name is used for display purposes and to identify
     * the unit in game events.
     * 
     * @return the name of the unit
     */
    public String getName() {
        return this.qualifier + StringConstants.SPACE + this.role;
    }

    /**
     * Getter for the team that owns this unit. The owner is assigned at
     * construction and determines which player controls the unit.
     * 
     * @return the team that owns this unit
     */
    public Team getOwner() {
        return this.owner;
    }

    /**
     * Getter for the unit's current position on the board. The position is updated
     * when the unit is placed on the board or moved to a new location.
     * 
     * @return the current position of the unit on the board (e.g., "A1", "B2"), or
     *         null if the unit is not currently on the board
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Sets the unit's position on the board to a new value. This method is called
     * when the unit is placed on the board or moved to a new location. If an empty
     * or null value is provided, the position is cleared (set to null).
     * 
     * @param newPos the new position to assign to the unit (e.g., "A1", "B2"), or
     *               null/empty to clear the position
     */
    public void setPosition(Position newPos) {
        if (newPos == null) {
            this.position = null; // Clear the position if an empty or null value is provided
            return;
        }
        this.position = newPos;
    }

    /**
     * Checks if the unit is currently hidden.
     * 
     * @return true if the unit is hidden, false otherwise
     */
    public boolean isHidden() {
        return this.isHidden;
    }

    /**
     * Sets the unit's hidden status.
     * 
     * @param hidden the new hidden status to assign to the unit (true to hide,
     *               false to reveal)
     */
    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    /**
     * Reveals the unit by setting its hidden status to false. Once a unit is
     * revealed, its true stats and information will be displayed in game events
     * and when viewing the unit's info.
     */
    public void faceUp() {
        this.isHidden = false;
    }

    /**
     * Checks if the unit is currently blocking an attack.
     * 
     * @return true if the unit is blocking, false otherwise
     */
    public boolean isBlocking() {
        return this.isBlocking;
    }

    /**
     * Checks if the unit has already moved this turn.
     * 
     * @return true if the unit has moved, false otherwise
     */
    public boolean hasMoved() {
        return this.hasMoved;
    }

    /**
     * Sets the unit's moved status for the turn.
     * 
     * @param hasMoved the new moved status to assign to the unit (true if the unit
     *                 has moved, false otherwise)
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
