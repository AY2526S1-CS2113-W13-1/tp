package seedu.zettel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all user interface interactions for Zettel.
 * The UI class manages input reading and output display,
 * such as welcome or goodbye messages, note listing and error messages.
 */
public class UI {
    private static final String LINE = "____________________________________________________________";
    private static final String LIST_INCOMING = "incoming";
    private static final String LIST_OUTGOING = "outgoing";
    private final Scanner scanner;

    /**
     * Constructs a UI object and initializes the Scanner for reading user input.
     */
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a command from the user input.
     *
     * @return The raw user input string, or "bye" if no input is available.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    /**
     * Displays the welcome message when the application starts,
     * including a list of available commands.
     */
    public void showWelcome() {
        printLine();
        System.out.println(" Hello! I'm Zettel");
        System.out.println(" What can I do for you?");
        printLine();
        System.out.println();
        showHelp();
        printLine();
    }

    /**
     * Displays the list of all available commands and their formats.
     * This method is called by HelpCommand and showWelcome.
     */
    public void showHelp() {
        System.out.println(" Available Commands:");
        System.out.println("   init <repo-name>                  - Initialize a new repository");
        System.out.println("   change-repo[pository] <repo-name> - Switch to another existing repository");
        System.out.println("   current-repo[pository]            - Show the name of the current repository");
        System.out.println("   new -t <title> [-b <body>]        - Create a new note");
        System.out.println("   edit <note-id>                    - Edit an existing note");
        System.out.println("   list [-p] [-a]                    - List all notes (or pinned only)");
        System.out.println("   delete [-f] <note-id>             - Delete a note by ID");
        System.out.println("   pin <note-id>                     - Pin a note");
        System.out.println("   unpin <note-id>                   - Unpin a note");
        System.out.println("   new-tag <tag-name>                - Adds a tag");
        System.out.println("   add-tag <note-id> <tag-name>      - Tag a note");
        System.out.println("   link <source-id> <target-id>      - Link two notes");
        System.out.println("   unlink <source-id> <target-id>    - Unlink two notes");
        System.out.println("   link-both <id1> <id2>             - Link two notes in both directions");
        System.out.println("   unlink-both <id1> <id2>           - Unlink two notes in both directions");
        System.out.println("   list-incoming-links <note-id>     - Show incoming linked notes");
        System.out.println("   list-outgoing-links <note-id>     - Show outgoing linked notes");
        System.out.println("   list-tags-all                     - Lists all tags that exist globally");
        System.out.println("   list-tags <note-id>               - List tags for an single note");
        System.out.println("   delete-tag [-f] <note-id> <tag>   - Delete a tag from a note");
        System.out.println("   delete-tag-globally [-f] <tag>    - Delete a tag from all notes");
        System.out.println("   rename-tag <old-tag> <new-tag>    - Rename a tag globally");
        System.out.println("   archive <note-id>                 - Moves note to archive folder");
        System.out.println("   unarchive <note-id>               - Moves note out of archive folder");
        System.out.println("   print-body <note-id>              - Print the body of a note");
        System.out.println("   find-note-by-body <search-terms>  - Search for notes by body content");
        System.out.println("   find-note-by-title <search-terms> - Search for notes by title");
        System.out.println("   help                              - Show this list of commands");
        System.out.println("   bye                               - Exit the application");
        System.out.println();
    }


    /**
     * Displays a message indicating that a deletion was cancelled.
     */
    public void showDeletionCancelled() {
        System.out.println("Deletion cancelled");
    }

    /**
     * Displays a message confirming that a note has been deleted.
     *
     * @param id The ID of the deleted note.
     */
    public void showNoteDeleted(String id) {
        System.out.println("Note at " + id + " deleted");
    }

    /**
     * Displays a confirmation prompt before deleting a note.
     *
     * @param id The ID of the note to delete.
     * @param noteTitle The title of the note to delete.
     */
    public void showDeleteNoteConfirmation(String id, String noteTitle) {
        System.out.println("Confirm deletion on '" + noteTitle + "', noteID " + id + 
                "? press y to confirm, any other key to cancel");
    }

    /**
     * Displays a confirmation prompt before deleting a tag.
     *
     * @param tag The tag to delete.
     */
    public void showDeleteTagConfirmation(String tag) {
        System.out.println("Confirm deletion of tag '" + tag + "'? press y to confirm, any other key to cancel");
    }

    /**
     * Displays a confirmation prompt before deleting a tag from a note.
     *
     * @param tag The tag to delete
     * @param noteId The ID of the note to delete.
     */
    public void showDeleteTagFromNoteConfirmation(String tag, String noteId) {
        System.out.println("Confirm deletion of tag '" + tag + "' on note # '" + noteId + 
                "'? press y to confirm, any other key to cancel");
    }


    /**
     * Displays a generic error message.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays the goodbye message when the application exits.
     */
    public void showBye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a message confirming that a note was added.
     *
     * @param note The note that was added.
     */
    public void showAddedNote(Note note) {
        System.out.println("Note created: " + note.getFilename() + " #" + note.getId());
    }

    /**
     * Displays a message when no pinned notes are found.
     */
    public void showNoPinnedNotes() {
        System.out.println("No pinned notes found. Pin a note to add to this list.\n");
    }

    /**
     * Displays a message when no notes exist in the repository.
     */
    public void showNoNotes() {
        System.out.println("No notes found.\n");
    }

    /**
     * Displays a list of notes.
     *
     * @param notes       The list of notes to display.
     * @param isPinned    True if displaying only pinned notes; false otherwise.
     * @param isArchived  True if displaying only archived notes; false otherwise.
     */
    public void showNoteList(List<Note> notes, boolean isPinned, boolean isArchived) {
        // Determine label based on flags
        String label;
        if (isPinned && isArchived) {
            label = "pinned and archived notes";
        } else if (isPinned) {
            label = "pinned notes";
        } else if (isArchived) {
            label = "archived notes";
        } else {
            label = "notes";
        }

        System.out.println("You have " + notes.size() + " " + label + ":");

        for (int idx = 0; idx < notes.size(); idx++) {
            System.out.println("    " + (idx + 1) + ". " + notes.get(idx));
        }
    }

    /**
     * Prints a horizontal line separator for formatting output.
     */
    public void printLine() {
        System.out.println(LINE);
    }

    /**
     * Closes the scanner.
     * Called when the application terminates.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays a message when no notes match a search query.
     */
    public void showNoNotesFound() {
        System.out.println("No notes found matching the search criteria.");
    }

    /**
     * Displays a list of notes that match a search query.
     *
     * @param matchedNotes The list of notes matching the search.
     */
    public void showFoundNotes(ArrayList<Note> matchedNotes) {
        System.out.println(" Here are the matching notes in your list:");
        for (int i = 0; i < matchedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + matchedNotes.get(i));
        }
    }

    /**
     * Displays a list of notes that match body search terms.
     *
     * @param matchedNotes The list of notes matching the search.
     * @param searchTerms The search terms used for the query.
     */
    public void showFoundNotesByBody(ArrayList<Note> matchedNotes, String searchTerms) {
        System.out.println(" Here are the notes with bodies matching the above:");
        for (int i = 0; i < matchedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + matchedNotes.get(i));
        }
    }

    /**
     * Displays a list of notes that match title search terms.
     *
     * @param matchedNotes The list of notes matching the search.
     * @param searchTerms The search terms used for the query.
     */
    public void showFoundNotesByTitle(ArrayList<Note> matchedNotes, String searchTerms) {
        System.out.println(" Here are the notes with titles matching the above:");
        for (int i = 0; i < matchedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + matchedNotes.get(i));
        }
    }

    /**
     * Displays a message confirming that a note has been pinned or unpinned.
     *
     * @param note The note being pinned or unpinned.
     * @param noteId The ID of the note.
     */
    public void showJustPinnedNote(Note note, String noteId) {
        System.out.println("Got it. I've " + (note.isPinned() ? "pinned" : "unpinned") + " this note: " + noteId);
    }

    /**
     * Displays a message confirming that a repository has been initialized.
     *
     * @param repoName The name of the newly created repository.
     */
    public void showRepoInit(String repoName) {
        System.out.println("Repository /" + repoName + " has been created.");
    }

    public void showSuccessfullyTaggedNote(String noteID, String tag) {
        System.out.println("Note #"+ noteID + " has been tagged with '"+ tag + "'");
    }

    public void showSuccessfullyAddedTag(String tag) {
        System.out.println("Tag '"+ tag + "' has been added.");
    }

    public void showOpeningEditor() {
        System.out.println("Opening editor for note body...");
    }

    public void showNoteSavedFromEditor() {
        System.out.println("Note body saved from editor.");
    }

    public void showNoteEdited(Note updatedNote) {
        System.out.println("Successfully edited note: " + updatedNote.getFilename() + ", id: " + updatedNote.getId());
    }

    public void showSuccessfulLinking(String referencingTitle, String linkedToTitle) {
        System.out.println("Note '" + referencingTitle + "' now links to note '" + linkedToTitle + "'.");
    }

    public void showLinkedNotes(ArrayList<Note> linkedNotes, String noteId, String listToShow) {
        if (listToShow.equals(LIST_INCOMING)) {
            System.out.println("Here are the notes that link to note #" + noteId + " (incoming):");
        } else if (listToShow.equals(LIST_OUTGOING)) {
            System.out.println("Here are the notes that note #" + noteId + " links to (outgoing):");
        }

        for (int i = 0; i < linkedNotes.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + linkedNotes.get(i));
        }
    }

    public void showSuccessfullyUnlinkedNotes(String sourceNoteId, String targetNoteId) {
        System.out.println("The link from note #" + sourceNoteId + " to note #" + targetNoteId + " has been removed.");
    }

    public void showSuccessfullyDoubleLinkedNotes(String noteTitle1, String noteTitle2) {
        System.out.println("Notes '" + noteTitle1 + "' and '" + noteTitle2 + "' are now linked in both directions.");
    }

    public void showSuccessfullyUnlinkedBothNotes(String noteId1, String noteId2) {
        System.out.println("All links between note #" + noteId1 + " and note #" + noteId2 + " have been removed.");
    }

    public void showArchivedNote(Note note) {
        System.out.println("Archived note: archive/" + note.getFilename());
    }

    public void showUnarchivedNote(Note note) {
        System.out.println("Unarchived note: " + note.getFilename() + " (moved to notes/)");
    }

    public void showTagsSingleNote(List<String> tags, String noteId) {
        System.out.println("Tags for note #" + noteId + ":");
        for (int i = 0; i < tags.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + tags.get(i));
        }
    }

    public void showTagsListGlobal(List<String> tags) {
        System.out.println("You have " + tags.size() + " tags:");
        // Show the list of tags.
        for (int idx = 0; idx < tags.size(); idx++) {
            System.out.println("    " + (idx + 1) + ". '" + tags.get(idx) + "'");
        }
    }

    public void showSuccessfullyDeletedTagFromNote(String noteId, String tag) {
        System.out.println("Tag '" + tag + "' has been deleted from note #" + noteId + ".");
    }

    public void showSuccessfullyDeletedTag(String tag) {
        System.out.println("Tag '" + tag + "' has been deleted across all notes, globally.");
    }

    public void showSuccessfullyRenamedTag(String oldTag, String newTag) {
        System.out.println("Tag '" + oldTag + "' has been renamed to '" + newTag
                + "' across all notes. All affected notes have been updated.");
    }

    public void showNoteBody(String noteId, String body) {
        System.out.println(" Body of note #" + noteId + ":");
        System.out.println(body);
    }

    public void showSuccessfullyRepoChanged(String repoName) {
        System.out.println("Successfully changed to repository: /" + repoName);

    }

    public void showCurrentRepo(String repoName) {
        System.out.println("Current repository: /" + repoName);
    }
}
