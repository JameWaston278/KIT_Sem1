package utils;

/**
 * A utility class for mathematical operations.
 *
 * @author udqch
 */
public final class MathUtils {
    private MathUtils() {
        // Private constructor to prevent instantiation of this utility class
    }

    /**
     * Computes the greatest common divisor (GCD) of two integers using the
     * Euclidean algorithm.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the greatest common divisor of a and b
     */
    public static int gcd(int a, int b) {
        int x = Math.abs(a);
        int y = Math.abs(b);
        while (y != 0) {
            int temp = y;
            y = x % y;
            x = temp;
        }
        return x;
    }

    /**
     * Checks if a given integer is a prime number.
     *
     * @param n the integer to check
     * @return true if n is prime, false otherwise
     */
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
