package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The RandomUtils class provides utility methods for performing weighted random
 * selections. It includes methods for both standard weighted random selection
 * and reverse weighted random selection, where higher weights are transformed
 * to
 * lower chances of selection.
 *
 * @author udqch
 */
public final class RandomUtils {

    private RandomUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Selects an index from the input list of weights based on a weighted random
     * selection.
     *
     * @param inputWeights A list of integer weights corresponding to each index.
     * @param random       An instance of Random to generate random numbers.
     * @return The index selected based on the weighted random selection.
     */
    public static int weightedRandom(List<Integer> inputWeights, Random random) {
        List<Integer> weights = new ArrayList<>();
        int totalWeight = 0;

        for (int weight : inputWeights) {
            int validWeight = Math.max(weight, 0); // Ensure weights are non-negative
            weights.add(validWeight);
            totalWeight += validWeight;
        }

        if (totalWeight == 0) {
            return 0;
        }

        int r = random.nextInt(totalWeight) + 1; // Generate a random number between 1 and totalWeight

        int runningSum = 0;
        for (int i = 0; i < weights.size(); i++) {
            runningSum += weights.get(i);
            if (r <= runningSum) {
                return i; // Return the index of the selected weight
            }
        }
        return 0;
    }

    /**
     * Selects an index from the input list of weights based on a reverse weighted
     * random selection.
     * The weights are transformed by subtracting the maximum weight from each
     * weight, effectively giving higher chances to lower weights.
     *
     * @param inputWeights A list of integer weights corresponding to each index.
     * @param random       An instance of Random to generate random numbers.
     * @return The index selected based on the reverse weighted random selection.
     */
    public static int inverseWeightedRandom(List<Integer> inputWeights, Random random) {
        // Find the maximum weight (k*) in the input list
        int kStar = Integer.MIN_VALUE;
        for (int weight : inputWeights) {
            if (weight > kStar) {
                kStar = weight;
            }
        }

        // Subtract k* from each weight to create a reverse list of weights
        List<Integer> newWeights = new ArrayList<>();
        for (int weight : inputWeights) {
            newWeights.add(weight - kStar);
        }

        return weightedRandom(newWeights, random);
    }
}
