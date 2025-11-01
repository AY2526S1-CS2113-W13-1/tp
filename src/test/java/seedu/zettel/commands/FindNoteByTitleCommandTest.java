package seedu.zettel.commands;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class FindNoteByTitleCommandTest {
    @Test
    public void testValidFindNoteByTitleCommandNoteIsFound() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("id-0", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        // FindNoteByTitleCommand searches in note title (case-insensitive)
        String searchTerms = "title 1";
        FindNoteByTitleCommand command = new FindNoteByTitleCommand(searchTerms);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        int sizeBefore = notes.size();
        assertDoesNotThrow(() -> command.execute(notes, tags, ui, storage));
        assertEquals(sizeBefore, notes.size(), "execute should not mutate the notes list size");
    }

    @Test 
    public void testNoNotesFound() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("id-0", "Title 0", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Title 1", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        String searchTerms = "Hello";
        FindNoteByTitleCommand command = new FindNoteByTitleCommand(searchTerms);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        int sizeBefore = notes.size();
        assertDoesNotThrow(() -> command.execute(notes, tags, ui, storage));
        assertEquals(sizeBefore, notes.size(), "execute should not mutate the notes list size");
    }

    @Test 
    public void testNoNotesAvailable() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        FindNoteByTitleCommand command = new FindNoteByTitleCommand("Title");
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        try {
            command.execute(notes, tags, ui, storage);
            org.junit.jupiter.api.Assertions.fail("Expected ZettelException when there are no notes");
        } catch (ZettelException ex) {
            assertTrue(ex.getMessage() == null || ex.getMessage().toLowerCase().contains("no notes"),
                    "Exception message should mention that there are no notes");
        }
    }

    @Test
    public void testMultipleSearchTerms() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("id-0", "My Note Title", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Another Title", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        String searchTerms = "Note Title";
        FindNoteByTitleCommand command = new FindNoteByTitleCommand(searchTerms);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        int sizeBefore = notes.size();
        assertDoesNotThrow(() -> command.execute(notes, tags, ui, storage));
        assertEquals(sizeBefore, notes.size(), "execute should not mutate the notes list size");
    }

    @Test
    public void testSpaceSeparatedTitleSearch() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("id-0", "title1 title2", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Another Title", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        String searchTerms = "title1 title2";
        FindNoteByTitleCommand command = new FindNoteByTitleCommand(searchTerms);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        int sizeBefore = notes.size();
        assertDoesNotThrow(() -> command.execute(notes, tags, ui, storage));
        assertEquals(sizeBefore, notes.size(), "execute should not mutate the notes list size");
    }

    @Test
    public void testPartialTitleMatch() {
        ArrayList<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        notes.add(new Note("id-0", "title1", "file0.txt", "Body 0", Instant.now(), Instant.now()));
        notes.add(new Note("id-1", "Another Title", "file1.txt", "Body 1", Instant.now(), Instant.now()));
        String searchTerms = "title1";
        FindNoteByTitleCommand command = new FindNoteByTitleCommand(searchTerms);
        UI ui = new UI();
        Storage storage = new Storage("build/testdata/findnote-test.txt");
        int sizeBefore = notes.size();
        assertDoesNotThrow(() -> command.execute(notes, tags, ui, storage));
        assertEquals(sizeBefore, notes.size(), "execute should not mutate the notes list size");
    }
}
