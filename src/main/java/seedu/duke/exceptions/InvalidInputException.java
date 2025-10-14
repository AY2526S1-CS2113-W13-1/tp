package seedu.duke.exceptions;

public class InvalidInputException extends ZettelException {
    public InvalidInputException(String message) {
        super("Invalid Input: " + message);
    }
}
