package seedu.zettel;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Zettel main class.
 * Tests application initialization and configuration.
 */
class ZettelTest {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path NOTES_FILE = DATA_DIR.resolve("notes.txt");

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(DATA_DIR);
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(NOTES_FILE);
            Files.deleteIfExists(DATA_DIR);
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    void constructor_initializesDependenciesSuccessfully() {
        assertDoesNotThrow(() -> {
            Zettel zettel = new Zettel();
            assertNotNull(zettel, "Zettel instance should not be null");
        });
    }

    @Test
    void constructor_createsUIAndStorageAndNotesList() {
        Zettel zettel = new Zettel();

        try {
            var uiField = Zettel.class.getDeclaredField("ui");
            var storageField = Zettel.class.getDeclaredField("storage");
            var notesField = Zettel.class.getDeclaredField("notes");

            uiField.setAccessible(true);
            storageField.setAccessible(true);
            notesField.setAccessible(true);

            assertNotNull(uiField.get(zettel), "UI should be initialized");
            assertNotNull(storageField.get(zettel), "Storage should be initialized");
            assertNotNull(notesField.get(zettel), "Notes list should be initialized");
        } catch (ReflectiveOperationException e) {
            fail("Reflection access failed: " + e.getMessage());
        }
    }

    @Test
    void mainMethod_existsAndDoesNotThrow() {
        assertDoesNotThrow(() -> {
            Zettel.class.getDeclaredMethod("main", String[].class);
        }, "Zettel should have a main method");
    }

    @Test
    void multipleInstances_canBeCreatedIndependently() {
        assertDoesNotThrow(() -> {
            Zettel z1 = new Zettel();
            Zettel z2 = new Zettel();
            assertNotSame(z1, z2, "Each Zettel instance should be unique");
        });
    }
}
