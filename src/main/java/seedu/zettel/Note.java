package seedu.zettel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Note {
    // Instance fields
    private final String id;
    private String title;
    private String filename; // actual filename on disk
    private String body;
    private Instant createdAt;
    private Instant modifiedAt;
    private boolean pinned;
    private boolean archived;
    private String archiveName;
    private List<String> logs; // history/log data

    // Static counter for tracking total notes
    private static int NUMBER_OF_NOTES = 0;

    // Date formatter for toString method
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    // Constructor for creating a new note
    public Note(String id, String title, String filename, String body, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.pinned = false;
        this.archived = false;
        this.archiveName = null;
        this.logs = new ArrayList<>();
        NUMBER_OF_NOTES++;
    }

    // Constructor with all fields (for loading from storage)
    public Note(String id, String title, String filename, String body,
                Instant createdAt, Instant modifiedAt, boolean pinned,
                boolean archived, String archiveName, List<String> logs) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.pinned = pinned;
        this.archived = archived;
        this.archiveName = archiveName;
        this.logs = logs != null ? new ArrayList<>(logs) : new ArrayList<>();
        NUMBER_OF_NOTES++;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public boolean isPinned() {
        return pinned;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs); // Return copy to maintain encapsulation
    }

    public static int getNumberOfNotes() {
        return NUMBER_OF_NOTES;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
        touchModified();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setBody(String body) {
        this.body = body;
        touchModified();
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        touchModified();
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
        touchModified();
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
        touchModified();
    }

    public void setLogs(List<String> logs) {
        this.logs = logs != null ? new ArrayList<>(logs) : new ArrayList<>();
    }

    public void addLog(String logEntry) {
        this.logs.add(logEntry);
    }

    // Helper method to update modifiedAt timestamp
    public void touchModified() {
        this.modifiedAt = Instant.now();
    }

    /**
     * Returns a formatted string representation of the note for display in lists.
     * Format: FILENAME yyyy-MM-dd NOTEID
     * Example: my_note.txt 2025-10-14 a1b2c3d4
     *
     * @return formatted string with filename, date, and note ID
     */
    @Override
    public String toString() {
        String formattedDate = DATE_FORMATTER.format(createdAt);
        return String.format("%s %s %s", filename, formattedDate, id);
    }

    /**
     * Returns a formatted string with line number prefix for numbered lists.
     * Format: INDEX. FILENAME yyyy-MM-dd NOTEID
     * Example: 1. my_note.txt 2025-10-14 a1b2c3d4
     *
     * @param index the line number to display
     * @return formatted string with index prefix
     */
    public String toStringWithIndex(int index) {
        return String.format("%d. %s", index, toString());
    }
}
