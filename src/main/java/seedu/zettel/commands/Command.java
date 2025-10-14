package seedu.zettel.commands;

import seedu.duke.exceptions.ZettelException;
import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.Storage;

import java.util.ArrayList;

public abstract class Command {

    public abstract Command execute(ArrayList<Note> notes, UI ui, Storage storage) throws ZettelException;

    public boolean isExit() {
        return false;
    }
}
