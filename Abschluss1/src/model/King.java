package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.Constants;

public class King extends Unit {
    public King(Team owner) {
        super(new UnitTemplate(Constants.KING_QUALIFIER, Constants.KING_ROLE, Constants.KING_ATK, Constants.KING_DEF),
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
