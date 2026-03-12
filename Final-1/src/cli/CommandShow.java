package cli;

import java.util.List;

import model.Team;
import model.Unit;
import utils.DisplayFormat;

/**
 * CommandShow is responsible for generating display information about a unit in
 * the game.
 * It formats the unit's name, team, attack, and defense stats based on whether
 * the unit is hidden and whether it belongs to the current player's team.
 * 
 * @author udqch
 */
final class CommandShow {
    private static final String MSG_NO_UNIT = "<no unit>";
    private static final String NEW_LINE_REGEX = "\\r?\\n";

    private CommandShow() {
        // Private constructor to prevent instantiation
    }

    /**
     * Executes the show command for a given unit and the current player's team.
     *
     * @param unit        The unit to display information about.
     * @param currentTeam The team of the current player, used to determine if
     *                    hidden information should be shown.
     * @return A list of strings representing the formatted information about the
     *         unit, which can be printed to the console. If the unit is null,
     *         returns a list containing a message indicating that there is no
     *         unit at the selected position.
     */
    static List<String> generateInfo(Unit unit, Team currentTeam) {
        if (unit == null) {
            return List.of(MSG_NO_UNIT);
        }

        var owner = unit.getOwner();
        if (unit.isKing()) {
            return List.of(DisplayFormat.KING_INFO.format(owner.getName()));
        }

        String nameToShow = unit.getName();
        String atkToShow = String.valueOf(unit.getAtk());
        String defToShow = String.valueOf(unit.getDef());
        if (unit.isHidden() && !owner.equals(currentTeam)) {
            String hiddenSymbol = DisplayFormat.HIDDEN_SYMBOL.getTemplate();
            nameToShow = hiddenSymbol;
            atkToShow = hiddenSymbol;
            defToShow = hiddenSymbol;
        }

        String infoString = DisplayFormat.UNIT_INFO.format(nameToShow, owner.getName(), atkToShow, defToShow);
        return List.of(infoString.split(NEW_LINE_REGEX));
    }
}
