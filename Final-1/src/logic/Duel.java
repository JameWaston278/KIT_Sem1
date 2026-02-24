package logic;

import exceptions.GameLogicException;
import model.Game;
import model.Team;
import model.Unit;
import utils.DisplayFormat;
import utils.EventLog;
import utils.StringConstants;

/**
 * The Duel class encapsulates the logic of a duel between an attacking unit and
 * a defending unit on the board.
 * It handles the resolution of the duel, including revealing hidden units,
 * calculating damage, and moving units if the attacker wins.
 * 
 * @author udqch
 */
public class Duel {
    private final Game game;
    private final Unit attacker;
    private final Unit defender;
    private final Team atkTeam;
    private final Team defTeam;
    private final String atkPos;
    private final String defPos;

    /**
     * Constructor for the Duel class.
     * 
     * @param game     The current game instance, used to access the board and
     *                 other game state.
     * @param attacker The unit that is attacking.
     * @param defender The unit that is defending.
     */
    public Duel(Game game, Unit attacker, Unit defender) {
        this.game = game;
        this.attacker = attacker;
        this.defender = defender;
        this.atkTeam = this.attacker.getOwner();
        this.defTeam = this.defender.getOwner();
        this.atkPos = this.attacker.getPosition();
        this.defPos = this.defender.getPosition();
    }

    /**
     * Resolves the duel by comparing the attacker's stats against the
     * defender's stats, taking into account special conditions such as blocking
     * or if the defender is a King.
     * 
     * @throws GameLogicException If there is an error during the duel resolution
     *                            (e.g., invalid positions, unit not found).
     */
    public void resolveDuel() throws GameLogicException {

        // Display notification of the attack.
        String hiddenSymbol = DisplayFormat.HIDDEN_SYMBOL.getTemplate();
        String defName = (this.defender.isHidden()) ? hiddenSymbol : this.defender.getName();
        String atkStats = String.format(hiddenSymbol, this.attacker.getAtk(), this.attacker.getDef());
        var defStats = (this.defender.isKing()) ? StringConstants.EMPTY
                : String.format(DisplayFormat.STATS.getTemplate(), this.defender.getAtk(), this.defender.getDef());

        System.out.println(
                EventLog.ATTACK.format(this.attacker.getName(), atkStats, defName, defStats,
                        this.defPos));

        // Reveal both units if they are hidden.
        if (this.attacker.isHidden()) {
            this.attacker.faceUp();
            System.out.println(
                    EventLog.FLIP.format(this.attacker.getName(), atkStats, this.defPos));
        }
        if (this.defender.isHidden()) {
            this.defender.faceUp();
            System.out.println(EventLog.FLIP.format(defName, defStats, this.atkPos));
        }

        // Resolve the duel by comparing ATK and DEF values.
        boolean duelResult; // True if the attacker wins, false otherwise.
        if (this.defender.isKing()) {
            duelResult = attackKing();
        } else if (this.defender.isBlocking()) {
            duelResult = attackBlockage();
        } else {
            duelResult = attackStandard();
        }

        // If the attacker wins, move the attacking unit into the defender's position.
        if (duelResult) {
            this.game.getBoard().moveUnit(this.atkPos, this.defPos);
            System.out.println(EventLog.MOVE.format(this.attacker.getName(), this.defPos));
        }
    }

    private boolean attackKing() {
        int damage = this.attacker.getAtk();
        this.defTeam.takeDamage(damage);
        System.out.println(EventLog.DAMAGE_TAKEN.format(this.defTeam.getName(), damage));
        return false;
    }

    private boolean attackBlockage() throws GameLogicException {
        if (this.attacker.getAtk() > this.defender.getDef()) {
            this.eliminateUnit(this.defender);
            return true; // Attacker wins and moves into the defender's position.
        } else if (this.attacker.getAtk() < this.defender.getDef()) {
            int damage = this.defender.getDef() - this.attacker.getAtk();
            atkTeam.takeDamage(damage);
            System.out.println(EventLog.DAMAGE_TAKEN.format(this.atkTeam.getName(), damage));
            return false; // Defender wins, attacker does not move.
        } else {
            return false; // No one wins if ATK and DEF are equal.
        }
    }

    private boolean attackStandard() throws GameLogicException {
        // Calculate the difference in ATK values to determine damage.
        int diff = Math.abs(this.attacker.getAtk() - this.defender.getAtk());

        if (this.attacker.getAtk() > this.defender.getAtk()) {
            // Attacker wins, defender is eliminated.
            defTeam.takeDamage(diff);
            this.eliminateUnit(this.defender);
            System.out.println(EventLog.DAMAGE_TAKEN.format(this.defTeam.getName(), diff));
            return true; // Attacker wins and moves into the defender's position.
        } else if (this.attacker.getAtk() < this.defender.getAtk()) {
            // Defender wins, attacker is eliminated.
            atkTeam.takeDamage(diff);
            this.eliminateUnit(this.attacker);
            System.out.println(EventLog.DAMAGE_TAKEN.format(this.atkTeam.getName(), diff));
            return false; // Defender wins, attacker does not move.
        } else {
            // ATK values are equal, both units are eliminated.
            this.eliminateUnit(this.defender);
            this.eliminateUnit(this.attacker);
            return false; // No one wins if ATK values are equal.
        }
    }

    private void eliminateUnit(Unit unit) throws GameLogicException {
        game.getBoard().removeUnitAt(unit.getPosition());
        System.out.println(EventLog.ELIMINATION.format(unit.getName()));
    }
}