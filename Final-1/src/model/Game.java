package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.EventLog;

/**
 * The Game class represents the overall state and logic of the game. It manages
 * the player and enemy teams, the game board, turn switching, and game over
 * conditions. It also initializes the game state based on provided parameters.
 * 
 * @author udqch
 */
public class Game {
    private static final Position PLAYER_KING_INITIAL_POSITION = new Position(3, 0);
    private static final Position ENEMY_KING_INITIAL_POSITION = new Position(3, 6);

    private final Board board;
    private final Team player;
    private final Team enemy;
    private Team currentTurn;
    private Team winner = null;

    private boolean hasPlaceUnitInTurn;
    private boolean isGameOver;

    /**
     * Constructor for the Game class, which initializes the game state with the
     * provided parameters. It sets up the player and enemy teams, initializes the
     * game board, and places the Kings on the board.
     * 
     * @param playerName The name of the player's team. If null, a default name
     *                   "Player" will be used.
     * @param enemyName  The name of the enemy's team. If null, a default name
     *                   "Enemy" will be used.
     * @param playerDeck The list of Unit objects representing the player's deck.
     * @param enemyDeck  The list of Unit objects representing the enemy's deck.
     * @throws GameLogicException If there is an error during game initialization,
     *                            such as invalid deck configurations or issues
     *                            placing units on the board.
     */
    public Game(String playerName, String enemyName, List<Unit> playerDeck, List<Unit> enemyDeck)
            throws GameLogicException {

        this.board = new Board();
        String playerTeamName = (playerName != null) ? playerName : "Player";
        String enemyTeamName = (enemyName != null) ? enemyName : "Enemy";
        this.player = new Team(playerTeamName, playerDeck);
        this.enemy = new Team(enemyTeamName, enemyDeck);

        this.isGameOver = false;

        initializeGame();
    }

    private void initializeGame() throws GameLogicException {
        // Both teams draw their initial hand
        player.drawInitialHand();
        enemy.drawInitialHand();

        // Place the Kings on the board
        King playerKing = new King(player);
        King enemyKing = new King(enemy);
        board.placeUnitAt(playerKing, PLAYER_KING_INITIAL_POSITION);
        board.placeUnitAt(enemyKing, ENEMY_KING_INITIAL_POSITION);

        currentTurn = player; // Player starts first
    }

    /**
     * Checks the win condition for both teams. A team wins if the opposing team is
     * defeated (LP <= 0) or if the opposing team has no more cards to draw.
     * If a win condition is met, the winner is set and the game is marked as over.
     * 
     * @param logs The list of logs to which win condition events will be added.
     */
    void checkWinCondition(List<String> logs) {
        if (isGameOver) {
            return; // If the game is already over, no need to check win conditions again.
        }

        if (player.isDefeated() || enemy.isDefeated()) {
            if (player.isDefeated()) {
                logs.add(EventLog.LP_DROPPED_TO_ZERO.format(player.getName()));
            }
            if (enemy.isDefeated()) {
                logs.add(EventLog.LP_DROPPED_TO_ZERO.format(enemy.getName()));
            }

            winner = player.isDefeated() ? enemy : player;
            isGameOver = true;
            logs.add(EventLog.WINS.format(winner.getName()));
        }
    }

    // --- TURN MANAGEMENT ---

    /**
     * Starts the turn for the specified team. This method resets the moved status
     * of all units on the team, allows the team to draw a card, and checks for
     * win conditions at the start of the turn.
     * 
     * @param team The team whose turn is starting.
     * @return A list of event log messages generated during progress.
     */
    public List<String> startTurn(Team team) {
        hasPlaceUnitInTurn = false;
        return TurnHelper.startTurn(this, team);
    }

    /**
     * Ends the current turn for the specified team. This method checks if the
     * team has a full hand and requires discarding a card, then switches the turn
     * to the other team and starts their turn.
     * 
     * @param team          The team whose turn is ending.
     * @param unitToDiscard The unit to discard from the team's hand if their hand
     *                      is full. If the hand is not full, this should be
     *                      null.
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If there is an error during end turn processing,
     *                            such as trying to discard a card when the hand
     *                            is not full, or not discarding a card when the
     *                            hand is full.
     */
    public List<String> endTurn(Team team, Unit unitToDiscard) throws GameLogicException {
        return TurnHelper.endTurn(this, team, unitToDiscard);
    }

    // --- GAME LOGIC ---

    /**
     * Executes a move for the specified team from the given starting position to
     * the target position. This method handles all the game logic involved in
     * moving a unit, including validating the move, resolving duels, combining
     * units, and checking for win conditions after the move.
     * 
     * @param team    The team making the move.
     * @param fromPos The starting position of the unit being moved.
     * @param toPos   The target position of the unit being moved.
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If the move is invalid according to game rules.
     */
    public List<String> executeMove(Team team, Position fromPos, Position toPos) throws GameLogicException {
        return MoveHelper.executeMove(this, team, fromPos, toPos);
    }

    /**
     * Executes a flip action for the specified team at the given position. This
     * method validates the flip action according to game rules and updates the
     * unit's hidden status if the action is valid.
     * 
     * @param team The team performing the flip action.
     * @param pos  The position of the unit that will be flipped.
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If the flip action is invalid according to game
     *                            rules.
     */
    public List<String> executeFlip(Team team, Position pos) throws GameLogicException {
        List<String> logs = new ArrayList<>();
        Unit unit = board.getUnitAt(pos);

        if (unit == null || !unit.getOwner().equals(team)) {
            throw new GameLogicException(ErrorMessage.INVALID_UNIT.format());
        }

        if (unit.hasMoved()) {
            throw new GameLogicException(ErrorMessage.UNIT_ALREADY_MOVED.format());
        }

        unit.setHidden(!unit.isHidden());
        unit.setHasMoved(true);
        logs.add(EventLog.FLIP.format(unit.getName(), unit.getAtk(), unit.getDef(), pos.toString()));
        return logs;
    }

    /**
     * Executes a block action for the specified team at the given position. This
     * method validates the block action according to game rules and updates the
     * unit's status to blocking if the action is valid.
     * 
     * @param team The team performing the block action.
     * @param pos  The position of the unit that will block.
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If the block action is invalid according to game
     *                            rules.
     */
    public List<String> executeBlock(Team team, Position pos) throws GameLogicException {
        List<String> logs = new ArrayList<>();
        Unit unit = board.getUnitAt(pos);

        if (unit == null || !unit.getOwner().equals(team)) {
            throw new GameLogicException(ErrorMessage.INVALID_UNIT.format());
        }

        if (unit.hasMoved()) {
            throw new GameLogicException(ErrorMessage.UNIT_ALREADY_MOVED.format());
        }

        unit.setBlocking(true);
        unit.setHasMoved(true);
        logs.add(EventLog.BLOCKS.format(team.getName(), unit.getName()));
        return logs;
    }

    /**
     * Handles the logic for placing a unit on the board. This method checks if the
     * player has already placed a unit this turn, validates the target position,
     * checks for valid hand indices, and manages the combination of units if the
     * target position is occupied by a friendly unit. It also updates the game
     * state and logs relevant events.
     *
     * @param team        The team placing the unit.
     * @param handIndices The list of indices from the player's hand representing
     *                    the units to be placed.
     * @param position    The target position on the board where the unit(s) should
     *                    be placed.
     * @return A list of event log messages generated during this action.
     * @throws GameLogicException If any validation fails during the placement
     *                            process,
     *                            such as invalid placement conditions or hand
     *                            indices.
     */
    public List<String> executePlace(Team team, List<Integer> handIndices, Position position)
            throws GameLogicException {
        return PlaceUnitHelper.placeUnit(this, team, handIndices, position);
    }

    /**
     * Eliminates a unit from the game by removing it from its owner's active units
     * and removing it from the board.
     * 
     * @param unit The unit to be eliminated.
     */
    void eliminateUnit(Unit unit) {
        Team team = unit.getOwner();
        team.removeActiveUnit(unit);
        board.removeUnitAt(unit.getPosition());
    }

    // --- GETTERS ---

    /**
     * Returns the game board.
     * 
     * @return The game board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns the team of the current player.
     * 
     * @return The current player's team.
     */
    public Team getPlayer() {
        return this.player;
    }

    /**
     * Returns the team of the enemy player.
     * 
     * @return The enemy player's team.
     */
    public Team getEnemy() {
        return this.enemy;
    }

    /**
     * Returns the team whose turn it currently is.
     * 
     * @return The current turn's team.
     */
    public Team getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Sets the current turn to the specified team.
     * 
     * @param team The team to set as the current turn.
     */
    void setCurrentTurn(Team team) {
        this.currentTurn = team;
    }

    /**
     * Returns the winner of the game.
     * 
     * @return The winner.
     */
    public Team getWinner() {
        return this.winner;
    }

    /**
     * Sets the winner of the game.
     * 
     * @param winner The winner.
     */
    void setWinner(Team winner) {
        this.winner = winner;
    }

    /**
     * Returns whether a unit has been placed during the current turn. This is used
     * to enforce the rule that only one unit can be placed per turn.
     * 
     * @return True if a unit has been placed during the current turn, false
     *         otherwise.
     */
    public boolean hasPlaceUnitInTurn() {
        return this.hasPlaceUnitInTurn;
    }

    /**
     * Sets whether a unit has been placed during the current turn. This should be
     * set to true when a unit is successfully placed, and reset to false at the
     * start of each turn.
     * 
     * @param hasPlaceUnitInTurn The value to set for whether a unit has been placed
     *                           during the current turn.
     */
    public void setHasPlaceUnitInTurn(boolean hasPlaceUnitInTurn) {
        this.hasPlaceUnitInTurn = hasPlaceUnitInTurn;
    }

    /**
     * Returns whether the game is over. The game is considered over if a win
     * condition has been met, such as one team being defeated.
     * 
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return this.isGameOver;
    }

    /**
     * Sets the game over status.
     * 
     * @param isGameOver The game over status to set.
     */
    void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }
}
