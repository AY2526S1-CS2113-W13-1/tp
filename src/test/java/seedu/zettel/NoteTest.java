package seedu.zettel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Note class.
 * Tests note creation, getters, setters, and hash-based ID functionality.
 */
public class NoteTest {

    private Note note;
    private final String id = "a1b2c3d4"; // 8-character lowercase hex ID
    private final String title = "Test Title";
    private final String filename = "test_note.txt";
    private final String body = "This is the body of the test note.";
    private final Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
    private final Instant modifiedAt = Instant.now();

    @BeforeEach
    void setUp() {
        // This setup runs before each test, providing a fresh note instance
        note = new Note(id, title, filename, body, createdAt, modifiedAt);
    }

    @Test
    @DisplayName("Test primary constructor for new note creation")
    void testNewNoteConstructor() {
        // The note from setUp is created with this constructor
        assertEquals(id, note.getId());
        assertEquals(title, note.getTitle());
        assertEquals(filename, note.getFilename());
        assertEquals(body, note.getBody());
        assertEquals(createdAt, note.getCreatedAt());
        assertEquals(modifiedAt, note.getModifiedAt());

        // Check default values for a new note
        assertFalse(note.isPinned(), "New note should not be pinned by default.");
        assertFalse(note.isArchived(), "New note should not be archived by default.");
        assertNull(note.getArchiveName(), "Archive name should be null for a new note.");
    }

    @Test
    @DisplayName("Test full constructor for loading a note")
    void testFullConstructor() {
        List<String> tags = List.of("Homework");
        Note loadedNote = new Note(id, title, filename, body, createdAt, modifiedAt,
                true, true, "my-archive", tags);

        assertEquals(id, loadedNote.getId());
        assertEquals(title, loadedNote.getTitle());
        assertTrue(loadedNote.isPinned(), "Pinned status should be loaded correctly.");
        assertTrue(loadedNote.isArchived(), "Archived status should be loaded correctly.");
        assertEquals("my-archive", loadedNote.getArchiveName());
        assertEquals(1, loadedNote.getTags().size());
        assertEquals("Homework", loadedNote.getTags().get(0));
    }


    @Test
    @DisplayName("Getters should return correct values")
    void testGetters() {
        assertEquals(id, note.getId());
        assertEquals(title, note.getTitle());
        assertEquals(filename, note.getFilename());
        assertEquals(body, note.getBody());
        assertEquals(createdAt, note.getCreatedAt());
        assertEquals(modifiedAt, note.getModifiedAt());
    }

    @Test
    @DisplayName("Setters should update values and modifiedAt timestamp")
    void testSettersAndUpdateTimestamp() throws InterruptedException {
        Instant initialModifiedAt = note.getModifiedAt();

        // Allow a millisecond to pass to ensure Instant.now() is different
        Thread.sleep(1);

        note.setTitle("New Title");
        assertEquals("New Title", note.getTitle());
        assertTrue(note.getModifiedAt().isAfter(initialModifiedAt), "setTitle should update modifiedAt.");

        initialModifiedAt = note.getModifiedAt();
        Thread.sleep(1);

        note.setBody("New body content.");
        assertEquals("New body content.", note.getBody());
        assertTrue(note.getModifiedAt().isAfter(initialModifiedAt), "setBody should update modifiedAt.");
    }

    @Test
    @DisplayName("setFilename should not update modifiedAt timestamp")
    void testSetFilenameDoesNotUpdateTimestamp() {
        Instant initialModifiedAt = note.getModifiedAt();
        note.setFilename("new_filename.md");
        assertEquals("new_filename.md", note.getFilename());
        assertEquals(initialModifiedAt, note.getModifiedAt(), "setFilename should not update modifiedAt.");
    }

    @Test
    @DisplayName("setPinned should update value and modifiedAt timestamp")
    void testSetPinned() throws InterruptedException {
        Instant initialModifiedAt = note.getModifiedAt();
        Thread.sleep(1);

        note.setPinned(true);
        assertTrue(note.isPinned());
        assertTrue(note.getModifiedAt().isAfter(initialModifiedAt), "setPinned should update modifiedAt.");
    }


    @Test
    @DisplayName("updateModifiedAt should update the modifiedAt timestamp")
    void testUpdateModifiedAt() throws InterruptedException {
        Instant initialModifiedAt = note.getModifiedAt();
        Thread.sleep(1); // Ensure the clock tick is registered
        note.updateModifiedAt();
        assertTrue(note.getModifiedAt().isAfter(initialModifiedAt),
                "updateModifiedAt should update the timestamp to a later time.");
    }

    @Test
    @DisplayName("toString should return correctly formatted string")
    void testToString() {
        String noteString = note.toString();

        // Expected format: "FILENAME yyyy-MM-dd NOTEID"
        // Example: test_note.txt 2025-10-14 a1b2c3d4
        String[] parts = noteString.split(" ");

        assertEquals(3, parts.length, "toString() output should have 3 parts separated by spaces.");
        assertEquals(filename, parts[0]);
        assertEquals(id, parts[2]);

        // Verify the date part matches the yyyy-MM-dd pattern
        assertTrue(parts[1].matches("\\d{4}-\\d{2}-\\d{2}"), "Date part should be in yyyy-MM-dd format.");
    }

    @Test
    @DisplayName("Static note counter should increment on object creation")
    void testNumberOfNotesCounter() {
        // This test is self-contained to avoid interference from other tests' object creations
        int countBefore = Note.getNumberOfNotes();

        // Use lowercase hex IDs
        Note note1 = new Note("abcd1234", "t1", "f1", "b1", Instant.now(), Instant.now());
        assertEquals(countBefore + 1, Note.getNumberOfNotes());

        Note note2 = new Note("def56789", "t2", "f2", "b2", Instant.now(), Instant.now());
        assertEquals(countBefore + 2, Note.getNumberOfNotes());
    }
}
