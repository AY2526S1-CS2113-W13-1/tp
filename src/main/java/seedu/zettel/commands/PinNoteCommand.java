package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.AlreadyPinnedException;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to pin or unpin a note by its ID.
 * Pinned notes can be listed separately for quick access.
 */
public class PinNoteCommand extends Command {
    private final boolean isPin;
    private final String noteId;

    /**
     * Constructs a PinNoteCommand with the specified note ID and pin status.
     *
     * @param noteId The 8-character hexadecimal note ID
     * @param isPin true to pin the note, false to unpin
     */
    public PinNoteCommand(String noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }
    
    /**
     * Executes the pin/unpin command on the specified note.
     *
     * @param notes   The list of all notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the note ID is invalid or the note doesn't exist
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        // Validation 1: Check if the notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to pin/unpin.");
        }

        // Validation 2: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();
        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }

        // Happy path: Execute the pin/unpin operation
        Note note = maybe.get();
        if (note.isPinned() == isPin) {
            throw new AlreadyPinnedException("Note with ID '" + noteId + "' is already " + 
                    (isPin ? "pinned." : "unpinned."));
        }
        note.setPinned(isPin);
        ui.showJustPinnedNote(note, noteId);
        storage.save(notes);
    }
}
