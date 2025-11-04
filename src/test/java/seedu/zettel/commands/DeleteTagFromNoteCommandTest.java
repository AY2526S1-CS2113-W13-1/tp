package seedu.zettel.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class DeleteTagFromNoteCommandTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;
    private final InputStream originalInputStream = System.in;
    private ArrayList<Note> notes;
    private UI ui;
    private Storage storage;
    private List<String> tags;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();

        // Create sample notes with tags
        Note note1 = new Note("abcd1234", "First Note", "First_Note.txt",
                "Body of first note", Instant.now(), Instant.now());
        note1.addTag("java");
        note1.addTag("programming");
        note1.addTag("tutorial");

        Note note2 = new Note("ef567890", "Second Note", "Second_Note.txt",
                "Body of second note", Instant.now(), Instant.now());
        note2.addTag("python");

        notes.add(note1);
        notes.add(note2);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutputStream);
        System.setIn(originalInputStream);
    }

    private void setInput(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
    }

    // ==================== Empty Notes List Test ====================

    @Test
    void testEmptyNotesListThrowsNoNotesException() {
        notes.clear();
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "java", true);

        ZettelException e = assertThrows(NoNotesException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("You have no notes to delete tags from.", e.getMessage());
    }

    // ==================== Note Not Found Tests ====================

    @Test
    void testNoteIdNotFoundThrowsInvalidNoteIdException() {
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("deadbeef", "java", true);

        ZettelException e = assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Invalid Note ID! Note with ID 'deadbeef' does not exist.", e.getMessage());
    }

    @Test
    void testNoteIdCaseSensitivity() {
        // Note IDs are lowercase hex, so uppercase should fail
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("ABCD1234", "java", true);

        assertThrows(InvalidNoteIdException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });
    }

    // ==================== Tag Not Found Tests ====================

    @Test
    void testTagNotFoundThrowsTagNotFoundException() {
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "nonexistent", true);

        ZettelException e = assertThrows(TagNotFoundException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });

        assertEquals("Tag 'nonexistent' does not exist for note with ID 'abcd1234'.", e.getMessage());
    }

    @Test
    void testTagExistsInOtherNoteButNotThisOne() {
        // "python" exists in note2 but not in note1
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "python", true);

        assertThrows(TagNotFoundException.class, () -> {
            cmd.execute(notes, tags, ui, storage);
        });
    }

    // ==================== Successful Deletion Tests ====================

    @Test
    void testSuccessfullyDeleteSingleTag_forceFlagSkipsConfirmation() throws ZettelException {
        Note note1 = notes.get(0);
        assertTrue(note1.getTags().contains("java"), "Note should have 'java' tag before deletion");
        assertEquals(3, note1.getTags().size(), "Note should have 3 tags before deletion");

        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "java", true);
        cmd.execute(notes, tags, ui, storage);

        assertFalse(note1.getTags().contains("java"), "Note should not have 'java' tag after deletion");
        assertEquals(2, note1.getTags().size(), "Note should have 2 tags after deletion");
        assertTrue(note1.getTags().contains("programming"), "Other tags should remain");
        assertTrue(note1.getTags().contains("tutorial"), "Other tags should remain");
    }

    @Test
    void testDeleteTagFromNoteWithMultipleTags_forceFlag() throws ZettelException {
        Note note1 = notes.get(0);
        
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "programming", true);
        cmd.execute(notes, tags, ui, storage);

        assertFalse(note1.getTags().contains("programming"));
        assertTrue(note1.getTags().contains("java"));
        assertTrue(note1.getTags().contains("tutorial"));
        assertEquals(2, note1.getTags().size());
    }

    @Test
    void testDeleteOnlyTagFromNote() throws ZettelException {
        Note note2 = notes.get(1); // Has only "python" tag
        assertEquals(1, note2.getTags().size(), "Note should have 1 tag before deletion");

        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("ef567890", "python", true);
        cmd.execute(notes, tags, ui, storage);

        assertTrue(note2.getTags().isEmpty(), "Note should have no tags after deleting the only tag");
    }

    @Test
    void testDeleteMultipleTagsSequentially() throws ZettelException {
        Note note1 = notes.get(0);

        // Delete first tag
        DeleteTagFromNoteCommand cmd1 = new DeleteTagFromNoteCommand("abcd1234", "java", true);
        cmd1.execute(notes, tags, ui, storage);
        assertEquals(2, note1.getTags().size());

        // Delete second tag
        DeleteTagFromNoteCommand cmd2 = new DeleteTagFromNoteCommand("abcd1234", "programming", true);
        cmd2.execute(notes, tags, ui, storage);
        assertEquals(1, note1.getTags().size());

        // Delete third tag
        DeleteTagFromNoteCommand cmd3 = new DeleteTagFromNoteCommand("abcd1234", "tutorial", true);
        cmd3.execute(notes, tags, ui, storage);
        assertTrue(note1.getTags().isEmpty());
    }

    @Test
    void testCannotDeleteSameTagTwice() throws ZettelException {
        DeleteTagFromNoteCommand cmd1 = new DeleteTagFromNoteCommand("abcd1234", "java", true);
        cmd1.execute(notes, tags, ui, storage);

        // Try to delete the same tag again
        DeleteTagFromNoteCommand cmd2 = new DeleteTagFromNoteCommand("abcd1234", "java", true);
        assertThrows(TagNotFoundException.class, () -> {
            cmd2.execute(notes, tags, ui, storage);
        });
    }

    @Test
    void testDeleteTagFromSecondNote() throws ZettelException {
        Note note2 = notes.get(1);
        assertTrue(note2.getTags().contains("python"));

        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("ef567890", "python", true);
        cmd.execute(notes, tags, ui, storage);

        assertFalse(note2.getTags().contains("python"));
    }

    @Test
    void testDeleteTagDoesNotAffectOtherNotes() throws ZettelException {
        Note note1 = notes.get(0);
        Note note2 = notes.get(1);
        int note2TagsBefore = note2.getTags().size();

        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "java", true);
        cmd.execute(notes, tags, ui, storage);

        // Note2 should be unaffected
        assertEquals(note2TagsBefore, note2.getTags().size(), 
                "Deleting tag from note1 should not affect note2");
        assertTrue(note2.getTags().contains("python"), 
                "Note2's tags should remain unchanged");
    }

    // ==================== Confirmation Flow Tests ====================

    @Test
    void testDeleteWithConfirmationYes_removesTagAndPrintsMessages() throws ZettelException {
        Note note1 = notes.get(0);
        assertTrue(note1.getTags().contains("java"));

        setInput("y\n");
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "java", false);
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Confirm deletion of tag 'java' on note # 'abcd1234'? Press y/Y to confirm, "
                + "any other key to cancel"));
        assertTrue(output.contains("Tag 'java' has been deleted from note #abcd1234."));
        assertFalse(note1.getTags().contains("java"));
    }

    @Test
    void testDeleteWithConfirmationNo_doesNotRemoveTagAndShowsCancelled() throws ZettelException {
        Note note1 = notes.get(0);
        assertTrue(note1.getTags().contains("java"));

        setInput("n\n");
        DeleteTagFromNoteCommand cmd = new DeleteTagFromNoteCommand("abcd1234", "java", false);
        cmd.execute(notes, tags, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("Confirm deletion of tag 'java' on note # 'abcd1234'? Press y/Y to confirm, "
                + "any other key to cancel"));
        assertTrue(output.contains("Deletion cancelled"));
        assertTrue(note1.getTags().contains("java"));
    }
}

