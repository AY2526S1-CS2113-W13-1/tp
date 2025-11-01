package seedu.zettel.exceptions;

/**
 * Exception thrown when an input is too long.
 * This exception is used to indicate issues with input length.
 */
public class LengthExceedException extends ZettelException {
    /**
     * Constructs a new LengthExceedException with the specified detail message.
     *
     * @param message The detail message explaining what input exceeded length.
     */
    public LengthExceedException(String message) {
        super(message);
    }
}
