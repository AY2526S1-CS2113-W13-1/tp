package seedu.zettel.commands;

import java.util.ArrayList;
import java.util.List;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.storage.Storage;

/**
 * Command to change the current repository to a different repository.
 * The repository must already exist (created via InitCommand).
 */
public class ChangeRepoCommand extends Command {

    private final String repoName;

    /**
     * Constructs a ChangeRepoCommand with the specified repository name.
     *
     * @param repoName The name of the repository to switch to
     */
    public ChangeRepoCommand(String repoName) {
        assert repoName != null : "Repo name should not be null.";
        this.repoName = repoName;
    }

    /**
     * Executes the command to change the current repository.
     * Validates that the repository exists before switching.
     *
     * @param notes   The list of notes (will be reloaded from new repo)
     * @param tags    The list of tags
     * @param ui      The UI instance for user interaction
     * @param storage The storage instance for repository management
     * @throws ZettelException If the repository does not exist
     */

    @Override
    public void execute(ArrayList<Note> notes, List<String> tags, UI ui, Storage storage)
            throws ZettelException {


        // Validate that the repository exists
        List<String> availableRepos = storage.getRepoList();
        if (!availableRepos.contains(repoName)) {
            throw new InvalidRepoException(
                    "Repository '" + repoName + "' does not exist. " + "Use 'init " + repoName + "' to create it first."
            );
        }

        // Check if already in the same repository
        String currentRepo = storage.readCurrRepo();
        if (repoName.equals(currentRepo)) {
            throw new InvalidRepoException("Already on " + repoName);
        }

        // Change to the new repository
        storage.changeRepo(repoName);

        // Clear current notes and reload from new repository
        notes.clear();
        notes.addAll(storage.load());

        // Clear current tags and reload from new repository
        tags.clear();
        tags.addAll(storage.readTagsLine());

        ui.showSuccessfullyRepoChanged(repoName);
    }
}
