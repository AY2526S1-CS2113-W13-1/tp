package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.Storage;

import java.util.ArrayList;

public abstract class Command {
    public abstract void result(ArrayList<Note> notes, UI ui, Storage storage);

    public boolean isExit() {
        return false;
    }
}
