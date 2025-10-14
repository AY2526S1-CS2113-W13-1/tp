package seedu.zettel.Commands;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import seedu.zettel.Note;
import seedu.zettel.commands.PinNoteCommand;

public class PinNoteCommandTest {
    @Test
    public void executeValidPinNoteCommandNoteIsPinned() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("id-0", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        Note target = notes.get(1);
        assertFalse(target.isPinned());

    Instant before = target.getModifiedAt();
    // Ensure clock advances on platforms with coarse Instant resolution
    Thread.sleep(2);
        new PinNoteCommand(1, true).execute(notes, null, null);
        assertTrue(target.isPinned());
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on pinning");
    }       

    @Test
    void executeUnpinsNoteAtValidIndexUpdatesPinnedAndModifiedAt() throws Exception {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note("id-0", "Title0", "file0.txt", "Body0",
                Instant.now(), Instant.now()));
        Note target = notes.get(0);

        target.setPinned(true);
        assertTrue(target.isPinned());

    Instant before = target.getModifiedAt();
    // Ensure clock advances on platforms with coarse Instant resolution
    Thread.sleep(2);
        new PinNoteCommand(0, false).execute(notes, null, null);

        assertFalse(target.isPinned(), "Note at index 0 should be unpinned");
        assertTrue(target.getModifiedAt().isAfter(before), "modifiedAt should be updated on unpin");
    }

    // @Test
    // void executeThrowsForNegativeIndex() {
    //     ArrayList<Note> notes = new ArrayList<>();
    //     notes.add(new Note("id-0", "Title0", "file0.txt", "Body0",
    //             Instant.now(), Instant.now()));

    // IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class,
    //     () -> new PinNoteCommand(-1, true).execute(notes, null, null));
    // assertTrue(ex.getMessage() == null || ex.getMessage().contains("does not exist"),
    //     "Exception message should mention missing note");
    // }

    // @Test
    // void executeThrowsForIndexBeyondSize() {
    //     ArrayList<Note> notes = new ArrayList<>();
    //     notes.add(new Note("id-0", "Title0", "file0.txt", "Body0",
    //             Instant.now(), Instant.now()));

    // IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class,
    //     () -> new PinNoteCommand(5, true).execute(notes, null, null));
    // assertTrue(ex.getMessage() == null || ex.getMessage().contains("does not exist"),
    //     "Exception message should mention missing note");
    // }


}
