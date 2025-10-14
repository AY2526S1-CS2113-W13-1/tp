package seedu.zettel.commands;

import java.util.ArrayList;

import seedu.zettel.Note;
import seedu.zettel.Storage;
import seedu.zettel.UI;
import java.util.Optional;
import java.util.HashSet;

public class PinNoteCommand extends Command {
    private final boolean isPin;
    private final int noteId;

    public PinNoteCommand(int noteId, boolean isPin) {
        this.noteId = noteId;
        this.isPin = isPin;
    }

    @Override
    public void execute(ArrayList<Note> notes, UI ui, Storage storage) {

        if (notes.size() <= noteId || noteId < 0) {
            throw new IndexOutOfBoundsException("Note with ID " + noteId + " does not exist.");
        }
        Note note = notes.get(noteId);
        note.setPinned(isPin);
        // Update modified timestamp when pin state changes
        note.touchModified();
        // ui.showNote(note);
        // storage.saveNote(note);
    }
}
