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
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.TagNotFoundException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

public class DeleteTagGloballyCommandTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private ArrayList<Note> notes;
    private List<String> tags;
    private UI ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        notes = new ArrayList<>();
        tags = new ArrayList<>();
        ui = new UI();
        storage = new Storage(tempDir.toString());
        storage.init();

        // Notes
        Note n1 = new Note("abcd1234", "First", "first.txt", "body1", Instant.now(), Instant.now());
        n1.addTag("java");
        n1.addTag("common");
        Note n2 = new Note("ef567890", "Second", "second.txt", "body2", Instant.now(), Instant.now());
        n2.addTag("python");
        n2.addTag("common");
        notes.add(n1);
        notes.add(n2);

        // Global tags
        tags.add("java");
        tags.add("python");
        tags.add("common");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void setInput(String s) {
        System.setIn(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void testForceDeletion_removesTagGloballyAndFromAllNotes() throws ZettelException {
        DeleteTagGloballyCommand cmd = new DeleteTagGloballyCommand("common", true);
        cmd.execute(notes, tags, ui, storage);

        // Removed from global
        assertFalse(tags.contains("common"));
        // Removed from all notes
        assertFalse(notes.get(0).getTags().contains("common"));
        assertFalse(notes.get(1).getTags().contains("common"));

        String out = outputStream.toString();
        assertTrue(out.contains("Tag 'common' has been deleted across all notes, globally."));
    }

    @Test
    void testConfirmationYes_deletesAndPrints() throws ZettelException {
        setInput("y\n");
        DeleteTagGloballyCommand cmd = new DeleteTagGloballyCommand("java", false);
        cmd.execute(notes, tags, ui, storage);

        assertFalse(tags.contains("java"));
        assertFalse(notes.get(0).getTags().contains("java"));

        String out = outputStream.toString();
        assertTrue(out.contains("Confirm deletion of tag 'java'? press y to confirm, any other key to cancel"));
        assertTrue(out.contains("Tag 'java' has been deleted across all notes, globally."));
    }

    @Test
    void testConfirmationNo_doesNotDeleteAndShowsCancelled() throws ZettelException {
        setInput("n\n");
        DeleteTagGloballyCommand cmd = new DeleteTagGloballyCommand("python", false);
        cmd.execute(notes, tags, ui, storage);

        // No changes
        assertTrue(tags.contains("python"));
        assertTrue(notes.get(1).getTags().contains("python"));

        String out = outputStream.toString();
        assertTrue(out.contains("Confirm deletion of tag 'python'? press y to confirm, any other key to cancel"));
        assertTrue(out.contains("Deletion cancelled"));
    }

    @Test
    void testNonExistentTag_throwsTagNotFound() {
        DeleteTagGloballyCommand cmd = new DeleteTagGloballyCommand("does-not-exist", true);
        ZettelException ex = assertThrows(
                TagNotFoundException.class,
                () -> cmd.execute(notes, tags, ui, storage)
        );
        assertEquals("Tag 'does-not-exist' does not exist.", ex.getMessage());
    }

    @Test
    void testEmptyTag_throwsInvalidFormat() {
        DeleteTagGloballyCommand cmd = new DeleteTagGloballyCommand(" ", true);
        ZettelException ex = assertThrows(InvalidFormatException.class,
                () -> cmd.execute(notes, tags, ui, storage));
        assertEquals("No tag provided to delete.", ex.getMessage());
    }
}
