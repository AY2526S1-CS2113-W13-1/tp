package seedu.zettel;


import seedu.duke.exceptions.ZettelException;
import seedu.duke.exceptions.EmptyDescriptionException;
import seedu.duke.exceptions.InvalidInputException;
import seedu.duke.exceptions.InvalidFormatException;
import seedu.duke.exceptions.InvalidIndexException;
import seedu.zettel.commands.Command;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.FindNoteCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.PinNoteCommand;


public class Parser {
    private static final String LIST_FORMAT = "List format should be: list [-p]";
    private static final String PIN_FORMAT = "Pin format should be: pin/unpin <NOTE_ID>";
    private static final String DELETE_FORMAT = "Delete format should be: delete [-f] <NOTE_ID> ";
    private static final String NOTE_FORMAT = "New note format should be: new -t <TITLE> [-b <BODY>]";
    private static final String NOTE_EMPTY = "Note title cannot be empty!";
    private static final String ID_EMPTY = "Please specify a Note ID to ";
    private static final String ID_INVALID = "Note ID must be exactly 6 Digits: eg. 123456 ";
    private static final String INIT_EMPTY = "Please specify a repo name!";

    public static Command parse(String userCommand) throws ZettelException {
        String[] inputs = userCommand.split(" ");
        String command = inputs[0].toLowerCase();

        return switch (command) {
        case "bye" -> new ExitCommand();
        case "list" -> parseListNoteCommand(userCommand);
        case "new" -> parseNewNoteCommand(userCommand);
        case "delete" -> parseDeleteNoteCommand(inputs);
        case "pin" -> parsePinNoteCommand(inputs, true);
        case "unpin" -> parsePinNoteCommand(inputs, false);
        case "init" -> parseInitCommand(userCommand);
        case "find" -> parseFindNoteCommand(userCommand);
        default -> throw new InvalidInputException(command);
        };

    }

    private static Command parseListNoteCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            return new ListNoteCommand(false);
        }
        else if (!content.equals("-p")) {
            throw new InvalidFormatException(LIST_FORMAT);
        }
        return new ListNoteCommand(true);
    }

    private static Command parseInitCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }
        return new InitCommand(content);
    }

    private static Command parseNewNoteCommand(String input) throws ZettelException {
        try {
            String content = input.substring(3).trim();

            int titleIndex = content.indexOf("-t");
            int bodyIndex = content.indexOf("-b");

            if (titleIndex == -1) {
                throw new InvalidFormatException(NOTE_FORMAT);
            }
            String title;
            String body = "";

            if (bodyIndex != -1) {
                title = content.substring(titleIndex + 2, bodyIndex).trim();
                body = content.substring(bodyIndex + 2).trim();
            } else {
                title = content.substring(titleIndex + 2).trim();
            }

            if (title.isEmpty()) {
                throw new EmptyDescriptionException(NOTE_EMPTY);
            }

            return new NewNoteCommand(title, body);

        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidFormatException(NOTE_FORMAT);
        }
    }

    private static Command parseFindNoteCommand(String input) throws ZettelException {
        String content = input.substring(4).trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException("Search cannot be empty!");
        }
        return new FindNoteCommand(content);
    }


    private static Command parsePinNoteCommand(String[] inputs, boolean isPin) throws ZettelException {
        if (inputs.length > 2) {
            throw new InvalidFormatException(PIN_FORMAT);
        }
        int noteID = parseNoteID(inputs, "pin/unpin");
        return new PinNoteCommand(noteID, isPin);
    }

    private static Command parseDeleteNoteCommand(String[] inputs) throws ZettelException {
        boolean forceDelete = false;
        if (inputs.length > 3) {
            throw new InvalidFormatException(DELETE_FORMAT);
        }
        else if (inputs.length == 3){
            if (!inputs[1].equals("-f")) {
                throw new InvalidInputException(DELETE_FORMAT);
            }
            forceDelete = true;
        }
        int noteID = parseNoteID(inputs, "delete");
        return new DeleteNoteCommand(noteID, forceDelete);
    }


    private static int parseNoteID(String[] inputs, String actionName) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(ID_EMPTY + actionName + "!");
        }
        String idString = inputs[inputs.length - 1].trim();

        if (idString.length() != 6) {
            throw new InvalidIndexException(ID_INVALID);
        }
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new InvalidIndexException(ID_INVALID);
        }
    }
}
