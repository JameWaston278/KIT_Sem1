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
    private static final int MAX_HAND_SIZE = 5;

    private final Board board;
    private final Team player;
    private final Team enemy;
    private Team currentTurn;
    private Team winner = null;

    private boolean isGameOver;
    private boolean hasPlaceUnitInTurn = false;

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
        List<String> logs = new ArrayList<>();
        hasPlaceUnitInTurn = false;
        team.resetAllMovedStatus();

        logs.add(EventLog.TURN_START.format(team.getName()));

        if (team.isDeckEmpty()) {
            // If the team has no cards left to draw, they lose immediately.
            winner = (team == player) ? enemy : player;
            isGameOver = true;
            logs.add(EventLog.DECK_EMPTY.format(team.getName()));
            logs.add(EventLog.WINS.format(winner.getName()));
            return logs;
        }

        team.drawCard();
        checkWinCondition(logs);
        return logs;
    }

    /**
     * Ends the current turn for the specified team. This method checks if the
     * team has a full hand and requires discarding a card, then switches the turn
     * to the other team and starts their turn.
     * 
     * @param team          The team whose turn is ending.
     * @param unitToDiscard The unit to discard from the team's hand if their hand
     *                      is full. If the hand is not full, this should be
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If there is an error during end turn processing,
     *                            such as trying to discard a card when the hand
     *                            is not full, or not discarding a card when the
     *                            hand is full.
     */
    public List<String> endTurn(Team team, Unit unitToDiscard) throws GameLogicException {
        List<String> logs = new ArrayList<>();

        if (team != currentTurn) {
            throw new GameLogicException(ErrorMessage.WRONG_TURN.format(team.getName()));
        }

        int currentHandSize = team.getHand().size();
        if (currentHandSize == MAX_HAND_SIZE) {
            if (unitToDiscard == null) {
                throw new GameLogicException(ErrorMessage.HAND_FULL.format(team.getName()));
            }
            team.discardCard(unitToDiscard);
            logs.add(EventLog.DISCARDED.format(
                    team.getName(), unitToDiscard.getName(), unitToDiscard.getAtk(), unitToDiscard.getDef()));
        } else {
            if (unitToDiscard != null) {
                throw new GameLogicException(ErrorMessage.HAND_NOT_FULL.format(team.getName()));
            }
        }

        currentTurn = (currentTurn == player) ? enemy : player;
        logs.addAll(startTurn(currentTurn));

        return logs;
    }

    /**
     * Checks the win condition for both teams. A team wins if the opposing team is
     * defeated (LP <= 0) or if the opposing team has no more cards to draw.
     * If a win condition is met, the winner is set and the game is marked as over.
     * 
     * @param logs The list of logs to which win condition events will be added.
     */
    public void checkWinCondition(List<String> logs) {
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

    // --- GAME LOGIC METHODS ---

    /**
     * Executes a move for the specified team from the given starting position to
     * the target position. This method validates the move according to game rules,
     * updates the game state accordingly, and checks for win conditions after the
     * move.
     * 
     * @param team    The team making the move.
     * @param fromPos The starting position of the unit being moved.
     * @param toPos   The target position of the unit being moved.
     * @throws GameLogicException If the move is invalid according to game rules.
     */
    public void executeMove(Team team, Position fromPos, Position toPos) throws GameLogicException {
        MoveHelper.executeMove(this, team, fromPos, toPos);
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
     * Returns the winner of the game.
     * 
     * @return The winner.
     */
    public Team getWinner() {
        return this.winner;
    }

    /**
     * Checks if the game is over.
     * 
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return this.isGameOver;
    }
}
