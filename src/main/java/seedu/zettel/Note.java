package seedu.zettel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a note in the Zettel system.
 * Each note has a unique 8-character hash-based ID, title, body, and metadata.
 */
public class Note {
    private static final Logger logger = Logger.getLogger(Note.class.getName());

    // Date formatter for toString method
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    // ID length constant - all note IDs must be exactly 8 characters
    private static final int ID_LENGTH = 8;

    // Static counter for tracking total notes created
    private static int numberOfNotes = 0;

    // Instance fields
    private final String id; // 8-character hash-based unique identifier
    private String title; // Title of the note
    private String filename; // Actual filename on disk
    private String body; // Body content of the note
    private Instant createdAt; // Timestamp when the note was created
    private Instant modifiedAt; // Timestamp when the note was last modified
    private boolean pinned; // Whether the note is pinned
    private boolean archived; // Whether the note has been archived
    private String archiveName; // Name of the archive the note belongs to
    private List<String> tags; // Tags for the note
    private HashSet<String> outgoingLinks; // note IDs that this note links to
    private HashSet<String> incomingLinks; // note IDs of notes that is linked by this note

    /**
     * Constructor for creating a new note by the user.
     * Initializes with default values for pinned and archived.
     *
     * @param id The unique 8-character hash-based ID
     * @param title The title of the note
     * @param filename The filename for storage on disk
     * @param body The body content of the note
     * @param createdAt The timestamp when the note was created
     * @param modifiedAt The timestamp when the note was last modified
     */
    public Note(String id, String title, String filename, String body,
                Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.pinned = false;
        this.archived = false;
        this.archiveName = null;
        this.tags = new ArrayList<>();
        this.outgoingLinks = new HashSet<>();
        this.incomingLinks = new HashSet<>();
        numberOfNotes++;
        logger.info("Sucessful note creation!");
    }

    /**
     * Constructor with all fields for loading a note from storage.
     * Validates that the ID is exactly 8 characters long.
     *
     * @param id The unique 8-character hash-based ID
     * @param title The title of the note
     * @param filename The filename for storage on disk
     * @param body The body content of the note
     * @param createdAt The timestamp when the note was created
     * @param modifiedAt The timestamp when the note was last modified
     * @param pinned Whether the note is pinned
     * @param archived Whether the note is archived
     * @param archiveName The archive name if archived, null otherwise
     */
    public Note(String id, String title, String filename, String body,
                Instant createdAt, Instant modifiedAt, boolean pinned,
                boolean archived, String archiveName, List<String> tags) {
        assert id.length() == ID_LENGTH : "Note ID must be " + ID_LENGTH + " characters long";
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.pinned = pinned;
        this.archived = archived;
        this.archiveName = archiveName;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.outgoingLinks = new HashSet<>();
        this.incomingLinks = new HashSet<>();
        numberOfNotes++;
    }

    // Getters

    /**
     * Gets the unique 8-character ID of this note.
     *
     * @return The note ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of this note.
     *
     * @return The note title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the filename used for storing this note on disk.
     *
     * @return The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Gets the body content of this note.
     *
     * @return The note body
     */
    public String getBody() {
        return body;
    }

    /**
     * Gets the timestamp when this note was created.
     *
     * @return The creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the timestamp when this note was last modified.
     *
     * @return The last modification timestamp
     */
    public Instant getModifiedAt() {
        return modifiedAt;
    }

    /**
     * Checks if this note is pinned.
     *
     * @return true if pinned, false otherwise
     */
    public boolean isPinned() {
        return pinned;
    }

    /**
     * Checks if this note is archived.
     *
     * @return true if archived, false otherwise
     */
    public boolean isArchived() {
        return archived;
    }

    /** 
     * Checks if another note is linked to this note.
     *
     * @param noteId The ID of the note to check
     * @return true if the note is linked, false otherwise
     */
    public boolean isLinkedTo(String noteId) {
        return outgoingLinks.contains(noteId);
    }

    /** 
     * Checks if another note is linked by this note.
     *
     * @param noteId The ID of the note to check
     * @return true if the note is linked, false otherwise
     */
    public boolean isLinkedBy(String noteId) {
        return incomingLinks.contains(noteId);
    }

    /**
     * Gets the archive name if this note is archived.
     *
     * @return The archive name, or null if not archived
     */
    public String getArchiveName() {
        return archiveName;
    }

    /**
     * Gets a tag list for this note.
     *
     * @return A new ArrayList containing the tag entries
     */

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }
    /**
     * Gets the total number of notes created since the application started.
     *
     * @return The total number of notes
     */
    public static int getNumberOfNotes() {
        return numberOfNotes;
    }

    /** 
     * Gets the set of noteIds that this note has outgoing
     * links to.
     *
     * @return a hash set of note IDs that this note links to
     */
    public HashSet<String> getOutgoingLinks() {
        return new HashSet<>(outgoingLinks);
    }

    /** 
     * Gets the set of noteIds of the incoming links that is linking
     * to this note.
     *
     * @return a hash set of note IDs that link to this note
     */
    public HashSet<String> getIncomingLinks() {
        return new HashSet<>(incomingLinks);
    }

    // Setters

    /**
     * Sets a new title for this note and updates the modified timestamp.
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
        updateModifiedAt();
    }

    /**
     * Sets a new filename for this note.
     * Does not update the modified timestamp.
     *
     * @param filename The new filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Sets new body content for this note and updates the modified timestamp.
     *
     * @param body The new body content
     */
    public void setBody(String body) {
        this.body = body;
        updateModifiedAt();
    }

    /**
     * Sets the pinned status of this note and updates the modified timestamp.
     *
     * @param pinned true to pin the note, false to unpin
     */
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        updateModifiedAt();
    }

    /**
     * Sets the archived status of this note and updates the modified timestamp.
     *
     * @param archived true to archive the note, false to unarchive
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
        updateModifiedAt();
    }

    /**
     * Sets the archive name for this note and updates the modified timestamp.
     *
     * @param archiveName The name of the archive
     */
    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
        updateModifiedAt();
    }


    public void addTag(String tag) {
        this.tags.add(tag);
    }

    /**
     * Removes a tag from this note's tags list.
     *
     * @param tag The tag to remove
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    /**
     * Adds a note ID to the "outgoingLinks" set.
     *
     * @param noteId The note ID to add
     */
    public void addOutgoingLink(String noteId) {
        if (noteId != null && !noteId.trim().isEmpty()) {
            this.outgoingLinks.add(noteId.trim());
        }
    }
    /**
     * Adds a note ID to the "incomingLinks" set.
     *
     * @param noteId The note ID to add
     */
    public void addIncomingLink(String noteId) {
        if (noteId != null && !noteId.trim().isEmpty()) {
            this.incomingLinks.add(noteId.trim());
        }
    }

    /**
     * Removes a note ID from the "outgoingLinks" set.
     *
     * @param noteId The note ID to remove
     */
    public void removeOutgoingLink(String noteId) {
        this.outgoingLinks.remove(noteId);
    }

    /**
     * Removes a note ID from the "incomingLinks" set.
     *
     * @param noteId The note ID to remove
     */
    public void removeIncomingLink(String noteId) {
        this.incomingLinks.remove(noteId);
    }

    /**
     * Updates the modifiedAt timestamp to the current time.
     * Called automatically by setters that modify note content.
     */
    public void updateModifiedAt() {
        this.modifiedAt = Instant.now();
    }

    /**
     * Load new body content for this note after storage parsing
     *
     * @param body The new body content
     */
    public void loadBody(String body) {
        this.body = body;
    }

    /**
     * Returns a formatted string representation of the note for display in lists.
     * Format: FILENAME yyyy-MM-dd NOTEID
     * Example: my_note.txt 2025-10-14 a1b2c3d4
     *
     * @return Formatted string with filename, creation date, and note ID
     */
    @Override
    public String toString() {
        String formattedDate = DATE_FORMATTER.format(createdAt);
        return String.format("%s %s %s", filename, formattedDate, id);
    }
}
