package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.Constants;

public class Unit {
    private String qualifier;
    private final String role;
    private final int atk;
    private final int def;
    private Team owner;

    private boolean isHidden;
    private boolean isBlocking;
    private boolean hasMoved;
    private boolean isDefeated;

    public Unit(UnitTemplate template, Team owner) {
        this.qualifier = template.getQualifier();
        this.role = template.getRole();
        this.atk = template.getAtk();
        this.def = template.getDef();
        this.owner = owner;

        this.isHidden = true;
        this.isBlocking = false;
        this.hasMoved = false;
        this.isDefeated = false;
    }

    // --- LOGIC METHODS ---

    public boolean isKing() {
        return false;
    }

    public void block() throws GameLogicException {
        if (this.hasMoved) {
            throw new GameLogicException(ErrorMessage.UNIT_ALREADY_MOVED.format(this.getName()));
        }
        this.isBlocking = true;
        this.hasMoved = true;
    }

    public String getInfo() {
        String displayName = (this.isHidden) ? Constants.HIDDEN_VALUE : this.getName();
        String displayOwner = (this.isHidden) ? Constants.HIDDEN_VALUE : this.owner.getName();
        String displayAtk = (this.isHidden) ? Constants.HIDDEN_VALUE : String.valueOf(this.atk);
        String displayDef = (this.isHidden) ? Constants.HIDDEN_VALUE : String.valueOf(this.def);
        return String.format(Constants.UNIT_DISPLAY_TEMPLATE, displayName, displayOwner, displayAtk, displayDef);
    }

    // --- GETTERS & SETTERS ---
    public int getAtk() {
        return this.atk;
    }

    public int getDef() {
        return this.def;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public String getRole() {
        return this.role;
    }

    public String getName() {
        return this.qualifier + Constants.WHITESPACE + this.role;
    }

    public Team getOwner() {
        return this.owner;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public boolean isBlocking() {
        return this.isBlocking;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public void setQualifier(String newQualifier) {
        this.qualifier = newQualifier;
    }

    public void faceUp() {
        this.isHidden = false;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void defeat() {
        this.isDefeated = true;
    }
}
