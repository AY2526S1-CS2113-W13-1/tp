package seedu.zettel.parser;

import java.util.Arrays;

import seedu.zettel.commands.ArchiveNoteCommand;
import seedu.zettel.commands.ChangeRepoCommand;
import seedu.zettel.commands.Command;
import seedu.zettel.commands.CurrentRepoCommand;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.DeleteTagFromNoteCommand;
import seedu.zettel.commands.DeleteTagGloballyCommand;
import seedu.zettel.commands.EditNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteByBodyCommand;
import seedu.zettel.commands.FindNoteByTitleCommand;
import seedu.zettel.commands.HelpCommand;
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.LinkBothNotesCommand;
import seedu.zettel.commands.LinkNotesCommand;
import seedu.zettel.commands.ListLinkedNotesCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.ListTagsGlobalCommand;
import seedu.zettel.commands.ListTagsSingleNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.NewTagCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.commands.PrintNoteBodyCommand;
import seedu.zettel.commands.RenameTagCommand;
import seedu.zettel.commands.TagNoteCommand;
import seedu.zettel.commands.UnlinkBothNotesCommand;
import seedu.zettel.commands.UnlinkNotesCommand;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Parses user input commands and converts them into executable Command objects.
 * This class handles validation and extraction of command parameters from raw user input strings.
 */
public class Parser {
    // Error messages to be printed when encountering errors
    // Categorised between empty, format and invalid
    private static final String LIST_FORMAT = "List format should be: list [-p] [-a], where -p and -a can be"
            + " in any order";
    private static final String PIN_FORMAT = "Pin format should be: pin/unpin <NOTE_ID>";
    private static final String INIT_FORMAT = "Init format should be: init <REPO_NAME>";
    private static final String DELETE_FORMAT = "Delete format should be: delete [-f] <NOTE_ID>";
    private static final String FIND_FORMAT = "Find format should be: find-note-by-body <SEARCH_TERMS>";
    private static final String FIND_BY_TITLE_FORMAT = 
            "Find by title format should be: find-note-by-title <SEARCH_TERMS>";
    private static final String NOTE_FORMAT = "New note format should be: new -t <TITLE> [-b <BODY>]";
    private static final String ADD_TAG_FORMAT = "Add tag command format should be: add-tag <NOTE_ID> <TAG>";
    private static final String NEW_TAG_FORMAT = "New tag command format should be: new-tag <TAG>"
            + ", where <TAG> is one word without spaces!";
    private static final String LIST_TAGS_GLOBAL_FORMAT = "List all tags globally command format should be: "
            + "list-tags-all";
    private static final String LINK_NOTES_FORMAT = "Link notes command format should be: link"
            + " <SOURCE_NOTE_ID> <TARGET_NOTE_ID>";
    private static final String UNLINK_NOTES_FORMAT = "Unlink notes command format should be: unlink" 
            + " <SOURCE_NOTE_ID> <TARGET_NOTE_ID>";
    private static final String LINK_BOTH_NOTES_FORMAT = "Linking both notes command format should be: link-both" 
            + " <NOTE_ID_1> <NOTE_ID_2>";
    private static final String UNLINK_BOTH_NOTES_FORMAT = "Unlinking both notes command format should be: unlink-both" 
            + " <NOTE_ID_1> <NOTE_ID_2>";
    private static final String LIST_TAGS_SINGLE_NOTE_FORMAT = "List tags for single note command format "
            + "should be: list-tags <NOTE_ID>";
    private static final String DELETE_TAG_FORMAT = "Delete tag command format should be: delete-tag [-f] "
            + "<NOTE_ID> <TAG>";
    private static final String DELETE_TAG_GLOBALLY_FORMAT = "Delete tag globally format should be: "
            + "delete-tag-globally [-f] <TAG>";
    private static final String RENAME_TAG_FORMAT = "Rename tag format should be: rename-tag <OLD_TAG> <NEW_TAG>";
    private static final String HELP_FORMAT = "Help format should be: help";
    private static final String NOTE_EMPTY = "Note title cannot be empty!";
    private static final String TAG_EMPTY = "Tag cannot be empty!";
    private static final String ID_EMPTY = "Please specify a Note ID to ";
    private static final String INIT_EMPTY = "Please specify a repo name!";
    private static final String INIT_INVALID =
            "Repo name can only contain alphanumeric characters, "
                    + "hyphens and underscores.";
    // Note ID validation constants
    private static final int VALID_NOTE_ID_LENGTH = 8;
    private static final String VALID_NOTE_ID_REGEX = "^[a-f0-9]{" + VALID_NOTE_ID_LENGTH + "}$";
    private static final String INVALID_ID_LENGTH_FORMAT =
            "Note ID must be exactly " + VALID_NOTE_ID_LENGTH + " characters long.";
    private static final String LIST_LINKED_TYPE_FORMAT = "Link type must be either" +
                    " 'incoming-links' or 'outgoing-links'.";
    private static final String LIST_LINKED_FORMAT = "List linked notes format should be: list "
                    + "<incoming-links/outgoing-links> <NOTE_ID>";
    private static final String ARCHIVE_NOTES_FORMAT = "Archive notes format should be: " +
                    "archive <NOTE_ID>";
    private static final String UNARCHIVE_NOTES_FORMAT = "Unarchive notes format should be: " +
                    "unarchive <NOTE_ID>";
    private static final String LIST_INCOMING_LINKS_FORMAT =
        "List incoming links format should be: list-incoming-links <NOTE_ID>";
    private static final String LIST_OUTGOING_LINKS_FORMAT =
        "List outgoing links format should be: list-outgoing-links <NOTE_ID>";
    private static final String CHANGE_REPO_FORMAT = "Change repo format should be: change-repo <REPO_NAME>";
    private static final String CHANGE_REPO_EMPTY = "Please specify a repo name to change to!";
    private static final String CURRENT_REPO_FORMAT = "Current repository format should be: current-repo\";";
    private static final String PRINT_NOTE_BODY_FORMAT =
        "Print note body format should be: print-body <NOTE_ID>";

    /**
     * Parses a user command string and returns the corresponding Command object.
     * Supported commands listed for each case.
     * Validate input with validator before parsing cases.
     *
     * @param input The raw user input string to parse.
     * @return A Command object corresponding to the user's input.
     * @throws ZettelException If the command format is invalid or parameters are missing.
     */
    public static Command parse(String input) throws ZettelException {
        Validator.validateCommandInput(input);
        String[] inputs = input.trim().split("\\s+"); //split input based on spaces in between
        String command = inputs[0].toLowerCase(); //first word of user input
        return switch (command) {
        case "bye" -> new ExitCommand();
        case "list" -> parseListNoteCommand(inputs);
        case "new" -> parseNewNoteCommand(inputs);
        case "edit" -> parseEditNoteCommand(inputs);
        case "delete" -> parseDeleteNoteCommand(inputs);
        case "pin" -> parsePinNoteCommand(inputs, true);
        case "unpin" -> parsePinNoteCommand(inputs, false);
        case "init" -> parseInitCommand(inputs);
        case "find-note-by-body" -> parseFindNoteByBodyCommand(inputs);
        case "find-note-by-title" -> parseFindNoteByTitleCommand(inputs);
        case "new-tag" -> parseNewTagCommand(inputs);
        case "add-tag" -> parseAddTagCommand(inputs);
        case "link" -> parseLinkNotesCommand(inputs);
        case "unlink" -> parseUnlinkNotesCommand(inputs);
        case "link-both" -> parseLinkBothNotesCommand(inputs);
        case "unlink-both" -> parseUnlinkBothNotesCommand(inputs);
        case "archive" -> parseArchiveNoteCommand(inputs, true);
        case "unarchive" -> parseArchiveNoteCommand(inputs, false);
        case "list-incoming-links" -> parseListIncomingLinksCommand(inputs);
        case "list-outgoing-links" -> parseListOutgoingLinksCommand(inputs);
        case "list-tags-all" -> parseListTagsGlobalCommand(inputs);
        case "list-tags" -> parseListTagsSingleNoteCommand(inputs);
        case "delete-tag" -> parseDeleteTagFromNoteCommand(inputs);
        case "delete-tag-globally" -> parseDeleteTagGloballyCommand(inputs);
        case "rename-tag" -> parseRenameTagCommand(inputs);
        case "print-body" -> parsePrintNoteBodyCommand(inputs);
        case "help" -> parseHelpCommand(inputs);
        case "change-repo", "change-repository" -> parseChangeRepoCommand(inputs);
        case "current-repo", "current-repository"  -> parseCurrentRepoCommand(inputs);
        default -> throw new InvalidInputException(command);
        };
    }


    /**
     * Parses a list command to display notes.
     * Accepts optional flags:
     *   -p  show only pinned notes
     *   -a  show only archived notes
     * Flags can be combined in any order (e.g. "list -a -p" or "list -p -a").
     * Routes to parseListLinkedNotesCommand only when the second token is NOT a flag.
     *
     * @param inputs The tokenized user input split by spaces
     * @return A ListNoteCommand or ListLinkedNotesCommand object with the appropriate parameters.
     * @throws ZettelException If the command format is invalid.
     */
    private static Command parseListNoteCommand(String[] inputs) throws ZettelException {

        if (inputs.length < 1) {
            throw new InvalidFormatException(LIST_FORMAT);
        }

        boolean showPinned = false;
        boolean showArchived = false;

        // parse flags if present (flags must start with '-' and be known)
        for (int i = 1; i < inputs.length; i++) {
            String tok = inputs[i].trim();
            if (!tok.startsWith("-")) {
                throw new InvalidFormatException(LIST_FORMAT);
            }
            switch (tok) {
            case "-p" -> {
                if (showPinned) {
                    throw new InvalidFormatException(LIST_FORMAT);
                }
                showPinned = true;
            }
            case "-a" -> {
                if (showArchived) {
                    throw new InvalidFormatException(LIST_FORMAT);
                }
                showArchived = true;
            }
            default -> throw new InvalidFormatException(LIST_FORMAT);
            }
        }

        return new ListNoteCommand(showPinned, showArchived);
    }

    /**
     * Parses an init command to initialize a new repository.
     * The repository name must contain only alphanumeric characters, hyphens, and underscores.
     *
     * @param inputs The tokenized user input split by spaces
     * @return An InitCommand object with the specified repository name.
     * @throws ZettelException If the repository name is empty or contains invalid characters.
     */
    private static Command parseInitCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }
        
        if (inputs.length != 2) {
            throw new InvalidFormatException(INIT_FORMAT);
        }

        String content = inputs[1].trim();
        if (content.isEmpty()) {
            throw new EmptyDescriptionException(INIT_EMPTY);
        }

        // Validate input - only alphanumeric, hyphens, and underscores
        if (!content.matches("[a-zA-Z0-9_-]+")) {
            throw new InvalidFormatException(INIT_INVALID);
        }
        return new InitCommand(content);
    }

    /**
     * Parses a new note command to create a new note with a title and optional body.
     * Expected format: new -t TITLE [-b BODY]
     *
     * @param inputs The tokenized user input split by spaces
     * @return A NewNoteCommand object with the extracted title and body.
     * @throws ZettelException If the format is invalid, title flag is missing, or title is empty.
     */
    private static Command parseNewNoteCommand(String[] inputs) throws ZettelException {
        try {
            String content = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length)).trim();

            int titleIndex = content.indexOf("-t");
            int bodyIndex = content.indexOf("-b");

            if (titleIndex == -1) {
                throw new InvalidFormatException(NOTE_FORMAT);
            }
            String title;
            String body = null; // newNoteCommand sees null as not provided, "" as user wants an empty body.

            if (bodyIndex != -1) {
                title = content.substring(titleIndex + 2, bodyIndex).trim();
                body = content.substring(bodyIndex + 2).trim();
            } else {
                title = content.substring(titleIndex + 2).trim();
            }


            if (title.isEmpty()) {
                throw new EmptyDescriptionException(NOTE_EMPTY);
            }

            Validator.validateTitleTag(title,"Title");

            return new NewNoteCommand(title, body);

        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidFormatException(NOTE_FORMAT);
        }
    }

    /**
     * Parses an edit note command to edit an existing note's body.
     * Expected format: edit NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces
     * @return An EditNoteCommand object with the extracted note ID
     * @throws ZettelException If the format is invalid or note ID is missing
     */
    private static Command parseEditNoteCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 2) {
            throw new InvalidFormatException("Invalid format. Usage: edit <NOTE_ID>");
        }
        String noteId = Validator.validateNoteId(inputs[1], "edit");
        return new EditNoteCommand(noteId);
    }

    /**
     * Parses a find-note-by-body command to search for notes by body content.
     * Expected format: find-note-by-body SEARCH_TERMS
     *
     * @param inputs The tokenized user input split by spaces
     * @return A FindNoteByBodyCommand object with the search query.
     * @throws ZettelException If the search query is empty.
     */
    private static Command parseFindNoteByBodyCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(FIND_FORMAT);
        }

        // Combine all search terms from index 1 onwards
        String searchTerms = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length)).trim();
        if (searchTerms.isEmpty()) {
            throw new EmptyDescriptionException(FIND_FORMAT);
        }
        return new FindNoteByBodyCommand(searchTerms);
    }

    /**
     * Parses a find-note-by-title command to search for notes by title.
     * Expected format: find-note-by-title SEARCH_TERMS
     *
     * @param inputs The tokenized user input split by spaces
     * @return A FindNoteByTitleCommand object with the search query.
     * @throws ZettelException If the search query is empty.
     */
    private static Command parseFindNoteByTitleCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(FIND_BY_TITLE_FORMAT);
        }

        // Combine all search terms from index 1 onwards
        String searchTerms = String.join(" ", Arrays.copyOfRange(inputs, 1, inputs.length)).trim();
        if (searchTerms.isEmpty()) {
            throw new EmptyDescriptionException(FIND_BY_TITLE_FORMAT);
        }
        return new FindNoteByTitleCommand(searchTerms);
    }

    /**
     * Parses a pin or unpin command to toggle a note's pinned status.
     * Expected format: pin/unpin NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @param isPin True if pinning the note, false if unpinning.
     * @return A PinNoteCommand object with the note ID and pin status.
     * @throws ZettelException If the format is invalid or note ID is missing/malformed.
     */
    private static Command parsePinNoteCommand(String[] inputs, boolean isPin) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(ID_EMPTY + (isPin ? "pin" : "unpin") + "!");
        }
        if (inputs.length > 2) {
            throw new InvalidFormatException(PIN_FORMAT);
        }
        String noteId = Validator.validateNoteId(inputs[1], isPin ? "pin" : "unpin");
        return new PinNoteCommand(noteId, isPin);
    }

    /**
     * Parses a delete command to remove a note.
     * Accepts an optional -f flag for force deletion.
     * Expected format: delete [-f] NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A DeleteNoteCommand object with the note ID and force flag.
     * @throws ZettelException If the format is invalid or note ID is missing/malformed.
     */
    private static Command parseDeleteNoteCommand(String[] inputs) throws ZettelException {
        // Expected formats:
        // 1) delete NOTE_ID
        // 2) delete -f NOTE_ID
        if (inputs.length < 2) {
            // Maintain existing behavior for missing NOTE_ID to keep tests green
            throw new EmptyDescriptionException(ID_EMPTY + "delete!");
        }

        switch (inputs.length) {
        case 2 -> {
            String noteId = Validator.validateNoteId(inputs[1], "delete");
            return new DeleteNoteCommand(noteId, false);
        }
        case 3 -> {
            if (!"-f".equals(inputs[1])) {
                throw new InvalidFormatException(DELETE_FORMAT);
            }
            String noteId = Validator.validateNoteId(inputs[2], "delete");
            return new DeleteNoteCommand(noteId, true);
        }
        default -> throw new InvalidFormatException(DELETE_FORMAT);
        }
    }

    /**
     * Parses a command to add a tag to a note.
     * Expected Format: add-tag NOTE_ID TAG_NAME
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A TagNoteCommand object with the note ID and tag.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseAddTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 3) {
            throw new InvalidFormatException(ADD_TAG_FORMAT);
        }

        String noteId = Validator.validateNoteId(inputs[1], "add-tag");
        String tag = inputs[2].trim();

        if (tag.isEmpty()) {
            throw new EmptyDescriptionException(TAG_EMPTY);
        }

        return new TagNoteCommand(noteId, tag);
    }

    /**
     * Parses a command to create a new global tag.
     * Expected Format: new-tag TAG_NAME
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A NewTagCommand object with the tag to add.
     * @throws ZettelException If the format is invalid or tag is missing.
     */
    private static Command parseNewTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length > 2) {
            throw new InvalidFormatException(NEW_TAG_FORMAT);
        }
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(TAG_EMPTY);
        }

        String tag = inputs[1];
        Validator.validateTitleTag(tag,"Tag");
        return new NewTagCommand(tag);
    }

    /**
     * Parses a command to list all tags globally across all notes.
     * Expected format: list-tags-all
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A ListTagsGlobalCommand object.
     * @throws ZettelException If the format is invalid.
     */
    private static Command parseListTagsGlobalCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 1) {
            throw new InvalidFormatException(LIST_TAGS_GLOBAL_FORMAT);
        }
        return new ListTagsGlobalCommand();
    }

    /**
     * Parses a link notes command to create a unidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return A LinkNotesCommand object with the note IDs to link.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */

    private static Command parseLinkNotesCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 3) {
            throw new InvalidFormatException(LINK_NOTES_FORMAT);
        }

        String sourceNoteId = Validator.validateNoteId(inputs[1], "link");
        String targetNoteId = Validator.validateNoteId(inputs[2], "link");

        return new LinkNotesCommand(sourceNoteId, targetNoteId);
    }

    /**
     * Parses a list-incoming-links command to display incoming links for a specific note.
     * Expected format: list-incoming-links NOTE_ID
     * 
     * @param inputs The tokenized user input split by spaces.
     * @return A ListLinkedNotesCommand object with the note ID.
     * @throws ZettelException If the format is invalid or note ID is malformed.
     */
    private static Command parseListIncomingLinksCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 2) {
            throw new InvalidFormatException(LIST_INCOMING_LINKS_FORMAT);
        }
        String noteId = Validator.validateNoteId(inputs[1], "list linked notes");
        return new ListLinkedNotesCommand("incoming", noteId);
    }

    /**
     * Parses a list-outgoing-links command to display outgoing links for a specific note.
     * Expected format: list-outgoing-links NOTE_ID
     * 
     * @param inputs The tokenized user input split by spaces.
     * @return A ListLinkedNotesCommand object with the note ID.
     * @throws ZettelException If the format is invalid or note ID is malformed.
     */
    private static Command parseListOutgoingLinksCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 2) {
            throw new InvalidFormatException(LIST_OUTGOING_LINKS_FORMAT);
        }
        String noteId = Validator.validateNoteId(inputs[1], "list linked notes");
        return new ListLinkedNotesCommand("outgoing", noteId);
    }

    /**
     * Parses an unlink notes command to remove a unidirectional link between two notes.
     * Expected format: unlink SOURCE_NOTE_ID TARGET_NOTE_ID
     *
     * @param inputs The tokenized user input split by spaces.
     * @return An UnlinkNotesCommand object with the source and target note IDs.
     * @throws ZettelException If the format is invalid or note IDs are malformed.
     */
    private static Command parseUnlinkNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: unlink <SOURCE_NOTE_ID> <TARGET_NOTE_ID>
        if (inputs.length != 3) {
            throw new InvalidFormatException(UNLINK_NOTES_FORMAT);
        }

        String sourceNoteId = Validator.validateNoteId(inputs[1], "unlink to");
        String targetNoteId = Validator.validateNoteId(inputs[2], "unlink to");

        return new UnlinkNotesCommand(sourceNoteId, targetNoteId);
    }

    /**
     * Parses a link-both notes command to create a bidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return A LinkBothNotesCommand object with the note IDs to link.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseLinkBothNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: link-both <note_ID_1> <note_ID_2>
        if (inputs.length != 3) {
            throw new InvalidFormatException(LINK_BOTH_NOTES_FORMAT);
        }

        String sourceNoteId = Validator.validateNoteId(inputs[1], "link in both directions");
        String targetNoteId = Validator.validateNoteId(inputs[2], "link in both directions");

        return new LinkBothNotesCommand(sourceNoteId, targetNoteId);
    }

    /**
     * Parses an unlink-both notes command to remove a bidirectional link between two notes.
     * @param inputs The tokenized user input split by spaces.
     * @return An UnlinkBothNotesCommand object with the note IDs to unlink.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */    
    private static Command parseUnlinkBothNotesCommand(String[] inputs) throws ZettelException {
        // Expected format: unlink-both <note_ID_1> <note_ID_2>
        if (inputs.length != 3) {
            throw new InvalidFormatException(UNLINK_BOTH_NOTES_FORMAT);
        }

        String noteId1 = Validator.validateNoteId(inputs[1], "unlink in both directions");
        String noteId2 = Validator.validateNoteId(inputs[2], "unlink in both directions");

        return new UnlinkBothNotesCommand(noteId1, noteId2);
    }

    /**
     * Parses an archive or unarchive command.
     * Expected format: archive/unarchive NOTE_ID
     *
     * @param inputs        The tokenized user input split by spaces
     * @param shouldArchive True for archive, false for unarchive
     * @return              An ArchiveNoteCommand object with the note ID and action
     * @throws ZettelException If the format is invalid or note ID is missing/malformed
     */
    private static Command parseArchiveNoteCommand(String[] inputs, boolean shouldArchive)
            throws ZettelException {
        if (inputs.length != 2) {
            throw (shouldArchive) ? new InvalidFormatException(ARCHIVE_NOTES_FORMAT)
                    : new InvalidFormatException(UNARCHIVE_NOTES_FORMAT);
        }
        String noteId = Validator.validateNoteId(inputs[1], shouldArchive ? "archive" : "unarchive");
        return new ArchiveNoteCommand(noteId, shouldArchive);
    }

    /**
     * Parses a list-tags command to display all tags associated with a specific note.
     * @param inputs The tokenized user input split by spaces.
     * @return A ListTagsSingleNoteCommand object with the note ID.
     * @throws ZettelException If the format is invalid or note ID is malformed.
     */
    private static Command parseListTagsSingleNoteCommand(String[] inputs) throws ZettelException {
        // expected format: list-tags <NOTE_ID>
        if (inputs.length != 2) {
            throw new InvalidFormatException(LIST_TAGS_SINGLE_NOTE_FORMAT);
        }

        String noteId = Validator.validateNoteId(inputs[1], "list tags for");
 
        return new ListTagsSingleNoteCommand(noteId);
    }

    /**
     * Parses a delete-tag command to remove a tag from a specific note.
     * @param inputs The tokenized user input split by spaces.
     * @return A DeleteTagFromNoteCommand object with the note ID and tag.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseDeleteTagFromNoteCommand(String[] inputs) throws ZettelException {
        // Expected formats:
        // 1) delete-tag NOTE_ID TAG_NAME
        // 2) delete-tag -f NOTE_ID TAG_NAME
        switch (inputs.length) {
        case 3 -> {
            String noteId = Validator.validateNoteId(inputs[1], "delete-tag");
            String tag = inputs[2].trim();
            if (tag.isEmpty()) {
                throw new EmptyDescriptionException(TAG_EMPTY);
            }
            return new DeleteTagFromNoteCommand(noteId, tag, false);
        }
        case 4 -> {
            if (!"-f".equals(inputs[1])) {
                throw new InvalidFormatException(DELETE_TAG_FORMAT);
            }
            String noteId = Validator.validateNoteId(inputs[2], "delete-tag");
            String tag = inputs[3].trim();
            if (tag.isEmpty()) {
                throw new EmptyDescriptionException(TAG_EMPTY);
            }
            return new DeleteTagFromNoteCommand(noteId, tag, true);
        }
        default -> throw new InvalidFormatException(DELETE_TAG_FORMAT);
        }
    }

    /**
     * Parses a delete-tag-globally command to remove a tag completely from the list,
     * thus removing the tag completely from all the notes.
     * 
     */
    private static Command parseDeleteTagGloballyCommand(String[] inputs) throws ZettelException {
        // Expected formats:
        // 1) delete-tag-globally TAG_NAME
        // 2) delete-tag-globally -f TAG_NAME
        switch (inputs.length) {
        case 2 -> {
            String tag = inputs[1].trim();
            if (tag.isEmpty()) {
                throw new EmptyDescriptionException(TAG_EMPTY);
            }
            return new DeleteTagGloballyCommand(tag, false);
        }
        case 3 -> {
            if (!"-f".equals(inputs[1])) {
                throw new InvalidFormatException(DELETE_TAG_GLOBALLY_FORMAT);
            }
            String tag = inputs[2].trim();
            if (tag.isEmpty()) {
                throw new EmptyDescriptionException(TAG_EMPTY);
            }
            return new DeleteTagGloballyCommand(tag, true);
        }
        default -> throw new InvalidFormatException(DELETE_TAG_GLOBALLY_FORMAT);
        }
    }

    /**
     * Parses a rename-tag command to rename an existing tag globally.
     * Expected Format: rename-tag OLD_TAG NEW_TAG
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A RenameTagCommand object with the old and new tag names.
     * @throws ZettelException If the format is invalid or parameters are missing.
     */
    private static Command parseRenameTagCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 3) {
            throw new InvalidFormatException(RENAME_TAG_FORMAT);
        }

        String oldTag = inputs[1].trim();
        String newTag = inputs[2].trim();

        if (oldTag.isEmpty() || newTag.isEmpty()) {
            throw new EmptyDescriptionException("Old tag or new tag cannot be empty!");
        }

        return new RenameTagCommand(oldTag, newTag);
    }

    /**
     * Parses a help command.
     * Expected format: help
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A HelpCommand object.
     * @throws ZettelException If the format is invalid.
     */
    private static Command parseHelpCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 1) {
            throw new InvalidFormatException(HELP_FORMAT);
        }
        return new HelpCommand();
    }
    /**
     * Parses a change-repo command to switch to a different repository.
     * Expected format: change-repo REPO_NAME
     *
     * @param inputs The tokenized user input split by spaces
     * @return A ChangeRepoCommand object with the specified repository name.
     * @throws ZettelException If the repository name is empty or format is invalid.
     */
    private static Command parseChangeRepoCommand(String[] inputs) throws ZettelException {
        if (inputs.length < 2) {
            throw new EmptyDescriptionException(CHANGE_REPO_EMPTY);
        }

        if (inputs.length != 2) {
            throw new InvalidFormatException(CHANGE_REPO_FORMAT);
        }

        String repoName = inputs[1].trim();
        if (repoName.isEmpty()) {
            throw new EmptyDescriptionException(CHANGE_REPO_EMPTY);
        }

        if (!repoName.matches("[a-zA-Z0-9_-]+")) {
            throw new InvalidFormatException(
                    "Repo name can only contain alphanumeric characters, hyphens and underscores.");
        }

        return new ChangeRepoCommand(repoName);
    }

    /**
     * Parses a current-repo command to display the currently active repository.
     * Expected format: current-repo
     *
     * @param inputs The tokenized user input split by spaces.
     * @return A CurrentRepoCommand object.
     * @throws ZettelException If the format is invalid.
     */
    private static Command parseCurrentRepoCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 1) {
            throw new InvalidFormatException(CURRENT_REPO_FORMAT);
        }
        return new CurrentRepoCommand();
    }

    /**
     * Parses a print-body command to display the body of a specific note.
     * Expected format: print-body NOTE_ID
     * 
     * @param inputs The tokenized user input split by spaces.
     * @return A Command object to print the note body.
     * @throws ZettelException If the format is invalid or note ID is malformed.
     */
    private static Command parsePrintNoteBodyCommand(String[] inputs) throws ZettelException {
        if (inputs.length != 2) {
            throw new InvalidFormatException(PRINT_NOTE_BODY_FORMAT);
        }
        String noteId = Validator.validateNoteId(inputs[1], "print-body");
        return new PrintNoteBodyCommand(noteId);
    }
}
