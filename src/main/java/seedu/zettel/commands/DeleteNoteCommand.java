package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidNoteIdException;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to delete a note by its ID.
 * Supports optional force deletion to skip confirmation prompt.
 * Also cleans up all links to and from the deleted note.
 */
public class DeleteNoteCommand extends Command {
    private final String noteId;
    private final boolean isForce;

    /**
     * Constructs a DeleteNoteCommand with the specified note ID and force flag.
     *
     * @param noteId The 8-character hexadecimal note ID to delete
     * @param force true to skip confirmation, false to prompt user
     */
    public DeleteNoteCommand(String noteId, boolean force) {
        this.noteId = noteId;
        this.isForce = force;
    }

    /**
     * Executes the delete command on the specified note.
     * Prompts for confirmation unless force flag is set.
     * Cleans up all incoming and outgoing links associated with the note.
     *
     * @param notes   The list of all notes
     * @param tags    The list of current tags.
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the notes list is empty or the note doesn't exist
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {

        // Validation 1: Check if the notes list is empty
        if (notes.isEmpty()) {
            throw new NoNotesException("You have no notes to delete.");
        }

        // Validation 2: Check if note with the given ID exists
        Optional<Note> maybe = notes.stream().filter(n -> n.getId().equals(noteId)).findFirst();

        if (maybe.isEmpty()) {
            throw new InvalidNoteIdException("Note with ID '" + noteId + "' does not exist.");
        }

        // Happy path: Execute the delete operation
        Note note = maybe.get();

        assert note != null : "Note should not be null";

        boolean shouldDelete = isForce;
        if (!isForce) {
            ui.showDeleteNoteConfirmation(noteId, note.getTitle());

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            shouldDelete = input.equals("y") || input.equals("yes");
        }

        if (shouldDelete) {
            // Clean up all links and text before deleting the note
            cleanupLinks(note, notes);
            storage.deleteStorageFile(note.getFilename());

            notes.remove(note);
            storage.save(notes);
            ui.showNoteDeleted(noteId);
        } else {
            ui.showDeletionCancelled();
        }
    }

    /**
     * Cleans up all incoming and outgoing links associated with the note being deleted.
     *
     * For each outgoing link: removes this note from the target note's incoming links.
     * For each incoming link: removes this note from the source note's outgoing links.
     *
     * @param noteToDelete The note that is being deleted
     * @param notes The list of all notes
     */
    private void cleanupLinks(Note noteToDelete, ArrayList<Note> notes) {
        String deletedNoteId = noteToDelete.getId();

        // Clean up outgoing links: for each note this note links to,
        // remove this note from their incoming links
        for (String targetNoteId : noteToDelete.getOutgoingLinks()) {
            notes.stream()
                    .filter(n -> n.getId().equals(targetNoteId))
                    .findFirst()
                    .ifPresent(targetNote -> targetNote.removeIncomingLink(deletedNoteId));
        }

        // Clean up incoming links: for each note that links to this note,
        // remove this note from their outgoing links
        for (String sourceNoteId : noteToDelete.getIncomingLinks()) {
            notes.stream()
                    .filter(n -> n.getId().equals(sourceNoteId))
                    .findFirst()
                    .ifPresent(sourceNote -> sourceNote.removeOutgoingLink(deletedNoteId));
        }
    }
}
