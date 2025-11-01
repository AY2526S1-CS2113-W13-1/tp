package seedu.zettel.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for ChangeRepoCommand.
 */
public class ChangeRepoCommandTest {

    private Path tempDir;
    private Storage storage;
    private UI ui;
    private ArrayList<Note> notes;
    private List<String> tags;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("zettel-change-repo-test");
        storage = new Storage(tempDir.toString());
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();

        storage.createRepo("main");
        storage.createRepo("projectX");
        storage.loadConfig();
    }

    @Test
    void execute_validRepo_switchesSuccessfully() {
        ChangeRepoCommand cmd = new ChangeRepoCommand("projectX");

        assertDoesNotThrow(() -> cmd.execute(notes, tags, ui, storage));

        String currentRepo = storage.readCurrRepo();
        assertEquals("projectX", currentRepo, "Expected repo to switch to 'projectX'");
    }

    @Test
    void execute_invalidRepo_throwsInvalidRepoException() {
        ChangeRepoCommand cmd = new ChangeRepoCommand("nonexistent_repo");

        InvalidRepoException ex = assertThrows(
                InvalidRepoException.class,
                () -> cmd.execute(notes, tags, ui, storage)
        );

        assertTrue(ex.getMessage().contains("does not exist"));
        assertTrue(ex.getMessage().contains("init"), "Error message should suggest using init");
    }

    @Test
    void execute_validRepo_reloadsNotesAndTags() throws ZettelException {
        storage.createRepo("dataRepo");
        storage.loadConfig();

        notes.add(new Note("123", "title", "file.txt", "body", null, null));
        tags.add("java");

        ChangeRepoCommand cmd = new ChangeRepoCommand("dataRepo");
        cmd.execute(notes, tags, ui, storage);

        assertTrue(notes.isEmpty(), "Notes list should be cleared and reloaded");
        assertTrue(tags.isEmpty(), "Tags list should be cleared and reloaded");
    }

    @Test
    void execute_sameRepo_throwsInvalidRepoException() {
        ChangeRepoCommand switchCmd = new ChangeRepoCommand("projectX");
        assertDoesNotThrow(() -> switchCmd.execute(notes, tags, ui, storage));

        ChangeRepoCommand sameCCmd = new ChangeRepoCommand("projectX");

        InvalidRepoException ex = assertThrows(
                InvalidRepoException.class,
                () -> sameCCmd.execute(notes, tags, ui, storage)
        );

        assertTrue(ex.getMessage().contains("Already on"));
        assertTrue(ex.getMessage().contains("projectX"));
    }
}
