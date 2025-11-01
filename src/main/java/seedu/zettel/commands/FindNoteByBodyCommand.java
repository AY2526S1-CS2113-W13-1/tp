package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.NoNotesException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to find notes containing given keywords in their body.
 * Performs a case-insensitive search across all existing notes within the current repository.
 */
public class FindNoteByBodyCommand extends Command{
    private final String searchTerms;

    /**
     * Constructs a FindNoteByBodyCommand with the specified search terms.
     *
     * @param searchTerms The search terms (can be multiple space-separated strings) to search for in note bodies.
     */
    public FindNoteByBodyCommand(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    /**
     * Executes the find command to search for notes containing the specified search terms.
     * The search is case-insensitive and matches any note whose body includes all the search terms.
     * Displays the matching notes or a "no results" message if none are found.
     *
     * @param notes   The list of existing notes
     * @param tags    The list of current tags
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for persistence
     * @throws ZettelException If the notes list is empty
     */
    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage) throws ZettelException {
        if (notes.isEmpty()) {
            throw new NoNotesException("There are no notes available to search.");
        }
        assert notes != null : "Notes list should not be null";
        
        ArrayList<Note> matchedNotes = new ArrayList<>();
        String lowerSearchTerms = searchTerms.toLowerCase();
        
        for (Note note: notes) {
            String lowerBody = note.getBody().toLowerCase();
            if (lowerBody.contains(lowerSearchTerms)) {
                matchedNotes.add(note);
            }
        }
        
        if (matchedNotes.isEmpty()) {
            ui.showNoNotesFound();
        } else {
            ui.showFoundNotesByBody(matchedNotes, searchTerms);
        }
    }
}
