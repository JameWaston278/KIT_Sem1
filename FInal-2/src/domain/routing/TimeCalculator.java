package domain.routing;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import domain.graph.Lift;
import domain.graph.Node;
import domain.graph.Piste;
import domain.skier.SkierProfile;

/****
 * The TimeCalculator class provides methods to calculate the time spent on a
 * given node (either a piste or a lift) based on the skier's profile and the
 * current time.
 *
 * @author udqch
 */
public final class TimeCalculator {

    /**
     * Calculates the time spent on a given node based on the skier's profile and
     * the current time.
     *
     * @param node        the node (piste or lift) for which to calculate the time
     *                    spent
     * @param currentTime the current time when the skier starts on the node
     * @param skier       the skier's profile containing their skill level and
     *                    preferences
     * @return an Optional containing the time spent in seconds, or an empty
     *         Optional if the node is not valid or if the lift is closed
     */
    public Optional<Long> calculateTimeSpent(Node node, LocalTime currentTime, SkierProfile skier) {

        switch (node) {
            case Piste piste -> {
                long timeSpent = calculatePisteTime(piste, skier);
                return Optional.of(timeSpent);
            }
            case Lift lift -> {
                long timeSpent = calculateLiftTime(lift, currentTime);
                if (timeSpent < 0) {
                    return Optional.empty(); // Lift is closed by the time the skier is ready to board
                }
                // Total time is the sum of waiting time, queue time, and ride time
                return Optional.of(timeSpent);
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    // Helper method to calculate time spent on a piste based on its attributes and
    // the skier's profile
    private long calculatePisteTime(Piste piste, SkierProfile skier) {
        double length = piste.getLength() / 8;
        double difficultyMod = piste.getDifficulty().getModifier();
        double surfaceMod = piste.getSurface().getModifier();
        double elevationDrop = (1 + 2 * piste.getElevationDrop());
        double skillMod = skier.getSkill().getModifier();

        long timeInSeconds = (long) (length * difficultyMod * surfaceMod * elevationDrop * skillMod);
        return timeInSeconds;
    }

    private long calculateLiftTime(Lift lift, LocalTime currentTime) {
        long rideSeconds = lift.getRideTime() * 60L;
        long queueSeconds = lift.getQueueTime() * 60L;

        // Check if the skier can board the lift before it closes
        LocalTime readyToBoard = currentTime.plusSeconds(queueSeconds);
        if (!readyToBoard.isBefore(lift.getEndTime())) {
            return -1; // Lift is closed by the time the skier is ready to board
        }

        // Calculate waiting time if the skier arrives before the lift starts operating
        long waitSeconds = 0;
        if (currentTime.isBefore(lift.getStartTime())) {
            waitSeconds = Duration.between(currentTime, lift.getStartTime()).getSeconds();
        }

        // Total time is the sum of waiting time, queue time, and ride time
        return waitSeconds + queueSeconds + rideSeconds;
    }
}
