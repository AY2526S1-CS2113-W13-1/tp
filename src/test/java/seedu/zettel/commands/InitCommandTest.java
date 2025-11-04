package seedu.zettel.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitCommandTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOutputStream = System.out;

    private UI ui;
    private Storage storage;
    private ArrayList<Note> notes;
    private List<String> tags;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStream));
        ui = new UI();
        notes = new ArrayList<>();
        tags = new ArrayList<>();
        storage = new Storage(tempDir.toString());
    }

    @AfterEach
    void teardown() {
        System.setOut(originalOutputStream);
    }

    @Test
    void testNewRepoInitShowsCreatedMessage() throws ZettelException {
        String repoName = "myRepo";

        // Execute the actual command
        InitCommand command = new InitCommand(repoName);
        command.execute(notes, tags, ui, storage);

        // Capture console output
        String output = outputStream.toString().trim().toLowerCase();

        // Assert output message contains expected text (case-insensitive)
        assertTrue(output.contains("repository /" + repoName.toLowerCase() + " has been created."),
                "Expected creation message not found. Output was:\n" + output);
    }

    @Test
    void testExecuteDuplicateRepoThrowsInvalidRepoException() throws ZettelException {
        String repoName = "myRepo";

        // First creation
        InitCommand command1 = new InitCommand(repoName);
        command1.execute(notes, tags, ui, storage);

        // Reload repo list from config to ensure the duplicate check works
        storage.loadConfig();

        // Second creation should fail
        InitCommand command2 = new InitCommand(repoName);
        InvalidRepoException exception = assertThrows(InvalidRepoException.class, () -> {
            command2.execute(notes, tags, ui, storage);
        });

        assertEquals("This repo already exists.", exception.getMessage());
    }
}
