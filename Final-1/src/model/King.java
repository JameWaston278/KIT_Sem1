package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.Constants;

/**
 * The King class represents the special King unit in the game. The King has
 * unique properties and behaviors compared to regular units, such as being
 * unable to block and having a special display format.
 * 
 * @author udqch
 */
public class King extends Unit {

    /**
     * The qualifier of the King unit.
     */
    public static final String KING_QUALIFIER = "Farmer";
    /**
     * The role of the King unit.
     */
    public static final String KING_ROLE = "King";
    /**
     * The attack value of the King unit, which is always 0.
     */
    public static final int KING_ATK = 0;
    /**
     * The defense value of the King unit, which is always 0.
     */
    public static final int KING_DEF = 0;

    /**
     * Constructor for the King class, which initializes a King unit with predefined
     * stats and properties based on the constants defined in Constants.
     * 
     * @param owner The team that owns this King unit.
     */
    public King(Team owner) {
        super(new UnitTemplate(KING_QUALIFIER, KING_ROLE, KING_ATK, KING_DEF),
                owner);
    }

    @Override
    public boolean isKing() {
        return true;
    }

    @Override
    public void block() throws GameLogicException {
        // The King cannot block, so this method just print error.
        throw new GameLogicException(ErrorMessage.KING_CANNOT_BLOCK.format());
    }

    @Override
    public String getInfo() {
        // The King return a special display string that only contains the team's name.
        return String.format(Constants.KING_DISPLAY_TEMPLATE, this.getOwner().getName());
    }

    @Override
    public void setQualifier(String newQualifier) {
        // The King's qualifier cannot be changed, so this method does nothing.
    }
}
