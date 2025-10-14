package seedu.zettel.commands;

import seedu.zettel.Note;
import seedu.zettel.UI;
import seedu.zettel.Storage;

import java.util.ArrayList;

public abstract class Command {

    public boolean isExit() {
        return false;
    }
}
