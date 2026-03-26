package utils;

import java.util.Optional;

/**
 * Utility class for parsing strings into enum constants. This class provides a
 * generic method to convert a string representation of an enum constant into
 * the
 * corresponding enum value, handling null and empty strings gracefully.
 *
 * @author udqch
 */
public final class EnumParser {

    private EnumParser() {
        // Private constructor to prevent instantiation
    }

    /**
     * Parses the given text into an enum constant of the specified enum type. If
     * the text is null, empty, or does not match any enum constant, this method
     * returns null.
     *
     * @param <T>      the type of the enum
     * @param enumType the class of the enum
     * @param text     the string representation of the enum constant
     * @return the corresponding enum constant or null if not found
     */
    public static <T extends Enum<T>> Optional<T> parseEnum(Class<T> enumType, String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equals(text.trim())) {
                return Optional.of(constant);
            }
        }
        return Optional.empty();
    }
}
