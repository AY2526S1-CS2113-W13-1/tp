package seedu.zettel.parser;

import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.ForbiddenCharacterFoundException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.LengthExceedException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Utility class for validating inputs.
 */
public class Validator {
    private static final int MAX_REPO_TITLE_TAG_LENGTH = 200;
    private static final int MAX_LENGTH = 3000;

    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{" + VALID_NOTE_ID_LENGTH + "}$";
    private static final String ID_EMPTY = "Please specify a Note ID to ";
    private static final String ID_INVALID = "Note ID must be exactly 8 hexadecimal characters (0-9, a-f)";
    private static final String INVALID_ID_LENGTH_FORMAT =
            "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.";
    private static final String INVALID_CHARS_FORMAT =
            "Input contains invalid characters. Only ASCII characters are allowed;";
    private static final String REPO_TITLE_TAG_INVALID_CHARS_FORMAT =
            " contains invalid characters. Only alphanumeric characters and space are allowed;";
    private static final String REPO_TITLE_FORMAT =
            " contains invalid characters. Only alphanumeric characters, underscores (_), and hyphens (-) are allowed.";
    private static final String PIPE_CHAR_ERROR = "Invalid character '|' detected in input!";
    private static final String REPO_TITLE_TAG_TOO_LONG =
            " must be less than " + MAX_REPO_TITLE_TAG_LENGTH + " characters.";
    private static final String INPUT_TOO_LONG =
            "Input must be less than " + MAX_LENGTH + " characters.";
    /**
     * Validates a note ID string.
     * The note ID must be exactly 8 lowercase hexadecimal characters.
     *
     * @param noteId The note ID string to validate
     * @param actionName The name of the action requesting validation (for error messages)
     * @return The validated note ID string (trimmed)
     * @throws ZettelException If the ID is null, empty, has incorrect length, or contains invalid characters
     */
    public static String validateNoteId(String noteId, String actionName) throws ZettelException {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new EmptyDescriptionException(ID_EMPTY + actionName + "!");
        }

        String idString = noteId.trim();
        assert !idString.isEmpty() : "ID string should not be empty after trim";

        // Validate noteId format - must be exactly 8 lowercase hex characters
        if (!idString.matches(VALID_NOTE_ID_REGEX)) {
            throw new InvalidFormatException(ID_INVALID);
        }

        if (idString.length() != VALID_NOTE_ID_LENGTH) {
            throw new InvalidFormatException(INVALID_ID_LENGTH_FORMAT);
        }

        return idString;
    }

    public static void validateCommandInput(String input) throws ZettelException {
        if (input == null) {
            return;
        }

        if (input.contains("|")) {
            throw new ForbiddenCharacterFoundException(PIPE_CHAR_ERROR);
        }

        if (input.length() > MAX_LENGTH) {
            throw new LengthExceedException(INPUT_TOO_LONG);
        }

        for (char c : input.toCharArray()) {
            if (c > 127) {
                throw new ForbiddenCharacterFoundException(INVALID_CHARS_FORMAT);
            }
        }
    }

    public static void validateRepoTitleTag(String input, String actionName) throws ZettelException {
        if (input == null) {
            return;
        }

        if (input.length() > MAX_REPO_TITLE_TAG_LENGTH) {
            throw new LengthExceedException(actionName + REPO_TITLE_TAG_TOO_LONG);
        }

        // Check for valid characters (alphanumeric and spaces only)
        if (!input.matches("^[a-zA-Z0-9 ]+$")) {
            throw new InvalidFormatException(actionName + REPO_TITLE_TAG_INVALID_CHARS_FORMAT);
        }

    }

    public static void validateRepoName(String input, String actionName) throws ZettelException {
        if (input == null) {
            return;
        }

        if (input.length() > MAX_REPO_TITLE_TAG_LENGTH) {
            throw new LengthExceedException(actionName + REPO_TITLE_TAG_TOO_LONG);
        }

        // Allow only alphanumeric, underscore (_), and hyphen (-)
        if (!input.matches("^[a-zA-Z0-9_-]+$")) {
            throw new InvalidFormatException(actionName + REPO_TITLE_FORMAT);
        }
    }
}
