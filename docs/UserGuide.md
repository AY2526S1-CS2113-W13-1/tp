# ZettelCLI User Guide

## Introduction

ZettelCLI is a desktop Command Line Interface (CLI) application used for managing a personal Zettelkasten note system. It is optimized for users who prefer keyboard commands over Graphical User Interfaces (GUI). Through just a keyboard, users can build their own repository of notes or quickly navigate through their knowledge base with ease.

## Quick Start
1. Ensure that you have Java 17 or above installed. 
2. Download the latest version of `ZettelCLI` from the releases page, on the right side of the home page. (https://github.com/AY2526S1-CS2113-W13-1/tp)
3. Run the application:
```bash
java -jar zettel.jar
```

## Command Summary

| Command | Format | Description |
|---------|--------|-------------|
| [Initialize Repository](#starting-a-zettelkasten-repository-init) | `init <repository-name>` | Create a new Zettelkasten repository |
| [Change Repository](#changing-repository-change-repo) | `change-repo <repository-name>` | Switch to another existing repository |
| [Current Repository](#viewing-current-repository-current-repo) | `current-repo` | Display the name of the current repository |
| [List Repositories](#listing-repositories) | `list-repos` | Lists all repositories initialized |
| [Add Note](#adding-a-new-note-new) | `new -t <TITLE> [-b <BODY>]` | Create a new note with optional body |
| [Edit Note](#editing-a-note-edit) | `edit <note-id>` | Edit an existing note |
| [List Notes](#listing-notes-with-filters-list) | `list [-p] [-a]` | List notes (with optional filters) |
| [Delete Note](#removing-a-note-delete) | `delete [-f] <note-id>` | Delete a note by ID |
| [Pin Note](#pinning-a-note-pin) | `pin <note-id>` | Pin a note to keep it at the top |
| [Unpin Note](#unpinning-a-note-unpin) | `unpin <note-id>` | Unpin a previously pinned note |
| [Archive Note](#archiving-a-note-archive) | `archive <note-id>` | Move note to archive folder |
| [Unarchive Note](#unarchiving-a-note-unarchive) | `unarchive <note-id>` | Move note out of archive folder |
| [Print Note Body](#printing-a-note-body-print-body) | `print-body <note-id>` | Display the full body of a note |
| [Find Notes by Body](#finding-notes-by-body-find-note-by-body) | `find-note-by-body <search-terms>` | Search for notes by body content |
| [Find Notes by Title](#finding-notes-by-title-find-note-by-title) | `find-note-by-title <search-terms>` | Search for notes by title |
| [Create Tag](#creating-a-tag-new-tag) | `new-tag <tag-name>` | Create a new global tag |
| [Add Tag to Note](#adding-a-tag-to-a-note-add-tag) | `add-tag <note-id> <tag-name>` | Tag a note with an existing or new tag |
| [List All Tags](#listing-all-tags-list-tags-all) | `list-tags-all` | List all global tags |
| [List Note Tags](#listing-tags-for-a-note-list-tags) | `list-tags <note-id>` | List all tags on a specific note |
| [Delete Tag from Note](#deleting-a-tag-from-a-note-delete-tag) | `delete-tag [-f] <note-id> <tag>` | Remove a tag from a specific note |
| [Delete Tag Globally](#deleting-a-tag-globally-delete-tag-globally) | `delete-tag-globally [-f] <tag>` | Delete a tag from all notes globally |
| [Rename Tag](#renaming-a-tag-rename-tag) | `rename-tag <old-tag> <new-tag>` | Rename a tag across all notes |
| [Link Notes](#linking-notes-link) | `link <source-id> <target-id>` | Create a one-way link from source to target |
| [Unlink Notes](#unlinking-notes-unlink) | `unlink <source-id> <target-id>` | Remove a one-way link from source to target |
| [Link Both Directions](#linking-notes-bidirectionally-link-both) | `link-both <id1> <id2>` | Create links in both directions |
| [Unlink Both Directions](#unlinking-notes-bidirectionally-unlink-both) | `unlink-both <id1> <id2>` | Remove all links between two notes |
| [List Incoming Links](#listing-incoming-links-list-incoming-links) | `list-incoming-links <note-id>` | Show notes that link to this note |
| [List Outgoing Links](#listing-outgoing-links-list-outgoing-links) | `list-outgoing-links <note-id>` | Show notes this note links to |
| [Help](#viewing-all-commands-help) | `help` | Display all available commands |
| [Exit](#exiting-the-application-bye) | `bye` | Exit the application |

---

## Features 

### Starting a Zettelkasten Repository: `init`

Initializes a new Zettelkasten repository. All repository names will be converted to lower case, thus duplicates are case-insensitive. This does not change the current repository, only creates a new one. User will have to use change-repo to change repository. (See: [Changing Repository](#changing-repository-change-repo))
 - Note: repository names only allow 2 special characters, dash (e.g. `-`) and underscore (e.g. `_`). All other speical characters are banned.

**Format:**
```
init <repository-name>
```

**Example:**
```
init My_New_Repo
```

**Expected Output:**
```
 Repository /my_new_repo has been created.
```

---

### Changing Repository: `change-repo`

Switches to another existing repository. The repository must have been previously created using the `init` command.
If already in the existing repository, no switching occurs. Repo names are case insensitive.


**Format:**
```
change-repo <repository-name>
```
or
```
change-repository <repository-name>
```

**Example:**
```
change-repo My_New_Repo
```

**Expected Output:**
```
 Successfully changed to repository: /my_new_repo
```

**Example (Already in Repository):**
```
change-repo main
```

**Expected Output:**
```
 Already on main!
```

---

### Viewing Current Repository: `current-repo`

Displays the name of the currently active repository. As per commands above, all repository names will be transformed to lower case.

**Format:**
```
current-repo
```
or
```
current-repository
```

**Expected Output:**
```
 Current repository: /my_new_repo
```

---

### Listing Repositories: `list-repos`

Displays list of repositories initialized.

**Format:**
```
list-repos
```
or
```
list-repositories
```

**Expected Output:**
```
 You have 2 repositories:
    1. /main
    2. /my_new_repo
```

---
### Adding a New Note: `new`

Creates a new note in the Zettelkasten repository. Notes can be created with only a title, or with both a title and a 
body. The ID generated is randomised.

**Note Title Requirements:**
- Can only contain **alphanumeric characters** (letters and numbers)
- Can be **space-separated** or a single word (e.g., `My Note` or `MyNote`)
- **No special characters allowed** (e.g., dashes, underscores, symbols)
- **No emojis allowed** in note titles
- Spaces in the title will be automatically converted to underscores in the filename
  - Example: `My New Note` becomes `My_New_Note.txt`
  - Example: `SimpleNote` becomes `SimpleNote.txt`

**Note Body:**
- Emojis can be used in the note body, but be aware that if your terminal or text editor does not support emojis, they may appear as strange characters (e.g., `??`)
- Ensure your editor and terminal have proper emoji support before using them in note content

**Format:**
```
new -t <TITLE> [-b <BODY>]
```

#### Creating a note with title only (opens text editor)
When you create a note with just a title and no `-b` flag, your **default text editor will open automatically**. The application will be blocked in the background until you close the editor (whether you save changes or not).

**Example:**
```
new -t New_Note
```

**Expected Output:**
```
 Opening editor for note body...
 Note body saved from editor.
 Note created: New_Note.txt #e0e7b989
```

#### Creating a note with an empty body (no editor)
When you use the `-b` flag **without providing a body argument**, the note is created with an empty body. The text editor will **NOT** open.

**Example:**
```
new -t New_Note -b
```

**Expected Output:**
```
 Note created: New_Note.txt #e0e7b989
```

#### Creating a note with title and body (no editor)
When you provide both a title and a body argument, the note is created immediately with the specified body. The text editor will **NOT** open.

**Example:**
```
new -t New_Note -b "This is a new note"
```

**Expected Output:**
```
 Note created: New_Note.txt #e0e7b989
```

---

### Editing a Note: `edit`

Opens an editor to modify the body of an existing note. Editor is based on the default editor of your system, can be notepad, vim etc.
The application is blocked in the background until the user closes the editor (be it after saving changes or not)
  - Note: archived notes are unable to be edited. One would have to bring it out of archive via `unarchive` command before attempt to edit (see `unarchive` command below).

**Format:**
```
edit <note-id>
```

**Example:** 
```
edit e0e7b989
```

**Expected Output:**
```
 Opening editor for note body...
 Note body saved from editor.
 Successfully edited note: New_Note.txt, id: e0e7b989
```

---

### Listing Notes (with filters): `list`

Lists notes with precise filters for pinned and archived states.

• `-p` shows only pinned notes.  
• `-a` shows only archived notes.  
You can combine flags in any order; behavior is defined by this matrix:

- `list` → pinned = X, archived = 0 (all unarchived notes)
- `list -a` → pinned = X, archived = 1 (all archived notes)
- `list -p` → pinned = 1, archived = 0 (pinned and unarchived)
- `list -p -a` → pinned = 1, archived = 1 (pinned and archived)
  - Note: X means both 1 and 0 are shown.

**Format:**
```
list [-p] [-a]
```

#### To view unarchived notes (pinned and unpinned)
Use the command without flags.

**Example:**
```
list
```

**Expected Output:**
```
 You have 3 notes:
    1. New_Note.txt 2025-10-17 e0e7b989
    2. brrr_againi.txt 2025-10-16 ccfd2e51
    3. file.txt 2025-10-16 55bb2cac
```

#### To view archived notes (pinned and unpinned)
Use the `-a` flag.

**Example:**
```
list -a
```

**Expected Output:**
```
 You have 2 notes (archived):
    1. Archived_Note.txt 2025-10-10 a1b2c3d4
    2. Old_Research.txt 2025-10-08 a9f0e1d2
```

#### To view pinned notes that are archived only
Use both `-p` and `-a` (in any order).

**Example:**
```
list -a -p
```

**Expected Output:**
```
 You have 1 pinned and archived notes:
    1. Archived_Note.txt 2025-10-10 a1b2c3d4
```

#### To view pinned notes that are unarchived only
Use the `-p` flag without `-a`.

**Example:**
```
list -p
```

**Expected Output:**
```
 You have 1 pinned notes:
    1. file.txt 2025-10-16 55bb2cac
```
---

### Removing a Note: `delete`

Removes the note at the specified index. Confirmation prevents accidental loss.

**Format:**
```
delete [-f] <NOTE_ID>
```

#### Delete with confirmation
To delete with confirmation, use the command with no flags.

**Example:**

```
delete e0e7b989 
```

**Expected Output:**

```
 Confirm deletion on 'New_Note', noteID e0e7b989? Press y/Y to confirm, 
 any other key to cancel
> y
 Note at e0e7b989 deleted
```

#### Delete without confirmation
Use the command with the `-f` flag.

**Example:**
```
delete -f 55bb2cac
```

**Expected Output:**
```
 Note at 55bb2cac deleted
```
---

### Pinning a Note: `pin`

Pins a note in the repository to keep it at the top of your list.

**Format:**

```
pin <NOTE_ID>
```

**Example:**

```
pin ccfd2e51
```

**Expected Output:**

```
 Got it. I've pinned this note: ccfd2e51
```

### Unpinning a Note: `unpin`

Unpins a note in the repository.

**Format:**

```
unpin <NOTE_ID>
```

**Example:**

```
unpin ccfd2e51
```

**Expected Output:**

```
 Got it. I've unpinned this note: ccfd2e51
```

---

### Archiving a Note: `archive`

Moves a note to the archive folder. There will be an archive folder for each of the repositories.
Archived notes can still be viewed using `list -a`.
  - Note: once a note is archived, it cannot be edited in the archived state. User would have to unarchive the note before it can be edited again, via `unarchive` command below.

**Format:**
```
archive <note-id>
```

**Example:**
```
archive e0e7b989
```

**Expected Output:**
```
 Archived note: archive/New_Note.txt
```

---

### Unarchiving a Note: `unarchive`

Moves a note out of the archive folder back to the main notes directory.
Can also be used to temporarily bring a note out of archive, with the purpose of editing the note (and possibly rearchiving it later again).

**Format:**
```
unarchive <note-id>
```

**Example:**
```
unarchive e0e7b989
```

**Expected Output:**
```
 Unarchived note: New_Note.txt (moved to notes/)
```

---

### Printing a Note Body: `print-body`

Prints the full body of a note by its ID. Note ID must be 8 lowercase hexadecimal characters.

**Format:**
```
print-body <NOTE_ID>
```

**Example:**
```
print-body abcd1234
```

**Expected Output:**
```
 Body of note #abcd1234:
 Hello World
```

---

### Finding Notes by Body: `find-note-by-body`

Searches for notes that contain the specified search terms in their body. You can provide multiple space-separated search terms, and the command will find notes whose body contains all of the specified terms.
 - Note: search is case insensitive.

**Format:**
```
find-note-by-body <search-terms>
```

**Example:**
```
find-note-by-body Zettelkasten system
```

**Expected Output:**
```
 Here are the notes with bodies matching the above:
 1. New_Note #e0e7b989
 2. Research #a1b2c3d4
```

---

### Finding Notes by Title: `find-note-by-title`

Searches for notes that contain the specified search terms in their title. You can provide multiple space-separated search terms, and the command will find notes whose title contains all of the specified terms.
 - Note: search is case insensitive.

**Format:**
```
find-note-by-title <search-terms>
```

**Example:**
```
find-note-by-title New Note
```

**Expected Output:**
```
 Here are the notes with titles matching the above:
 1. New_Note #e0e7b989
 2. New_Research #ccfd2e51
```

---

### Tagging System

ZettelCLI uses a global tagging system that allows you to organize notes across all repositories. Tags are created globally and can be used by any repository and any note.

**Tag Naming Requirements:**
- Can only contain **alphanumeric characters** (letters and numbers)
- **No spaces allowed**
- **No special characters** (e.g., `?`, `%`, `!`, `_`, `-` are not allowed)
- Examples:
  - Allowed: `important`, `toDoLater`
  - Not allowed: `my tag` (contains space), `urgent!!` (contains `!`), `test_tag` (contains `_`), `my-tag` (contains `-`)

#### Creating a Tag: `new-tag`

Creates a new global tag that is not initially linked to any note. This tag becomes available for use across all repositories.

**Format:**
```
new-tag <tag-name>
```

**Example:**
```
new-tag important
```

**Expected Output:**
```
 Tag 'important' has been added.
```

---

#### Adding a Tag to a Note: `add-tag`

Assigns a tag to a specific note. If the tag does not exist globally, it will be created automatically and then assigned to the note.

**Format:**
```
add-tag <note-id> <tag-name>
```

**Example:**
```
add-tag e0e7b989 important
```

**Expected Output:**
```
 Note #e0e7b989 has been tagged with 'important'
```

---

#### Listing All Tags: `list-tags-all`

Displays all global tags that have been created across all repositories.

**Format:**
```
list-tags-all
```

**Expected Output:**
```
 You have 3 tags:
    1. 'important'
    2. 'research'
    3. 'todo'
```

---

#### Listing Tags for a Note: `list-tags`

Shows all tags assigned to a specific note.

**Format:**
```
list-tags <note-id>
```

**Example:**
```
list-tags e0e7b989
```

**Expected Output:**
```
 Tags for note #e0e7b989:
 1. important
 2. research
```

---

#### Deleting a Tag from a Note: `delete-tag`

Removes a tag from a specific note without deleting the tag globally. The tag remains available for other notes.

**Format:**
```
delete-tag [-f] <note-id> <tag>
```

**Example (with confirmation):**
```
delete-tag e0e7b989 important
```

**Expected Output:**
```
 Confirm deletion of tag 'important' on note # 'e0e7b989'? Press y/Y to confirm, 
 any other key to cancel
> y

 Tag 'important' has been deleted from note #e0e7b989.
```

**Example (without confirmation):**
```
delete-tag -f e0e7b989 important
```

**Expected Output:**
```
 Tag 'important' has been deleted from note #e0e7b989.
```

---

#### Deleting a Tag Globally: `delete-tag-globally`

Deletes a tag from all notes across all repositories and removes it from the global tag list.
 - Note: deletion is case sensitive.

**Format:**
```
delete-tag-globally [-f] <tag>
```

**Example (with confirmation):**
```
delete-tag-globally important
```

**Expected Output:**
```
 Confirm deletion of tag 'important'? Press y/Y to confirm, any other key to cancel
> y
 Tag 'important' has been deleted across all notes, globally.
```

**Example (without confirmation):**
```
delete-tag-globally -f important
```

**Expected Output:**
```
 Tag 'important' has been deleted across all notes, globally.
```

---

#### Renaming a Tag: `rename-tag`

Renames a tag globally across all notes in all repositories.

**Format:**
```
rename-tag <old-tag> <new-tag>
```

**Example:**
```
rename-tag important critical
```

**Expected Output:**
```
 Tag 'important' has been renamed to 'critical' across all notes. 
 All affected notes have been updated.
```

---

### Linking System

ZettelCLI allows you to create directional links between notes to build a knowledge graph. Links can be one-way or bidirectional.

#### Linking Notes: `link`

Creates a one-way link from a source note to a target note, establishing the relationship: **source → target**.

**Format:**
```
link <source-id> <target-id>
```

**Example:**
```
link e0e7b989 ccfd2e51
```
This creates the relationship: `e0e7b989 → ccfd2e51`

**Expected Output:**
```
 Note 'New_Note' now links to note 'brrr_againi'.
```

---

#### Unlinking Notes: `unlink`

Removes a one-way link from source to target. This destroys the **source → target** relationship if it exists.

**Format:**
```
unlink <source-id> <target-id>
```

**Example:**
```
unlink e0e7b989 ccfd2e51
```
This removes the relationship: `e0e7b989 → ccfd2e51`

**Expected Output:**
```
 The link from note #e0e7b989 to note #ccfd2e51 has been removed.
```

---

#### Linking Notes Bidirectionally: `link-both`

Creates links in both directions between two notes, establishing the relationships: **id1 → id2** and **id2 → id1**.

**Format:**
```
link-both <id1> <id2>
```

**Example:**
```
link-both e0e7b989 ccfd2e51
```
This creates the relationships: `e0e7b989 → ccfd2e51` and `ccfd2e51 → e0e7b989`

**Expected Output:**
```
 Notes 'New_Note' and 'brrr_againi' are now linked in both directions.
```

---

#### Unlinking Notes Bidirectionally: `unlink-both`

Removes all links between two notes in both directions. This destroys both the **id1 → id2** and **id2 → id1** relationships.

**Format:**
```
unlink-both <id1> <id2>
```

**Example:**
```
unlink-both e0e7b989 ccfd2e51
```
This removes the relationships: `e0e7b989 → ccfd2e51` and `ccfd2e51 → e0e7b989`

**Expected Output:**
```
 All links between note #e0e7b989 and note #ccfd2e51 have been removed.
```

---

#### Listing Incoming Links: `list-incoming-links`

Shows all notes that link to the specified note (incoming links).

**Format:**
```
list-incoming-links <note-id>
```

**Example:**
```
list-incoming-links ccfd2e51
```

**Expected Output:**
```
 Here are the notes that link to note #ccfd2e51 (incoming):
 1. New_Note.txt 2025-10-17 e0e7b989
```

---

#### Listing Outgoing Links: `list-outgoing-links`

Shows all notes that the specified note links to (outgoing links).

**Format:**
```
list-outgoing-links <note-id>
```

**Example:**
```
list-outgoing-links e0e7b989
```

**Expected Output:**
```
 Here are the notes that note #e0e7b989 links to (outgoing):
 1. brrr_againi.txt 2025-10-16 ccfd2e51
```

---

### Viewing All Commands: `help`

Displays a complete list of available commands and their descriptions.

**Format:**

```
help
```

**Expected Output:**

```
 Available Commands:
   init <repo-name>                  - Initialize a new repository
   change-repo[pository] <repo-name> - Switch to another existing repository
   current-repo[pository]            - Show the name of the current repository
   list-repos                        - Lists all repositories initialized
   new -t <title> [-b <body>]        - Create a new note
   edit <note-id>                    - Edit an existing note
   list [-p] [-a]                    - List notes (pinned and/or archived filters)
   delete [-f] <note-id>             - Delete a note by ID
   pin <note-id>                     - Pin a note
   unpin <note-id>                   - Unpin a note
   new-tag <tag-name>                - Adds a tag
   add-tag <note-id> <tag-name>      - Tag a note
   link <source-id> <target-id>      - Link two notes
   unlink <source-id> <target-id>    - Unlink two notes
   link-both <id1> <id2>             - Link two notes in both directions
   unlink-both <id1> <id2>           - Unlink two notes in both directions
   list-incoming-links <note-id>     - Show incoming linked notes
   list-outgoing-links <note-id>     - Show outgoing linked notes
   list-tags-all                     - Lists all tags that exist globally
   list-tags <note-id>               - List tags for an single note
   delete-tag [-f] <note-id> <tag>   - Delete a tag from a note
   delete-tag-globally [-f] <tag>    - Delete a tag from all notes
   rename-tag <old-tag> <new-tag>    - Rename a tag globally
   archive <note-id>                 - Moves note to archive folder
   unarchive <note-id>               - Moves note out of archive folder
   print-body <note-id>              - Print the body of a note
   find-note-by-body <search-terms>  - Search for notes by body content
   find-note-by-title <search-terms> - Search for notes by title
   help                              - Show this list of commands
   bye                               - Exit the application
```

---

### Exiting the Application: `bye`

Exits ZettelCLI and saves all changes.

**Format:**
```
bye
```

**Expected Output:**
```
 Bye. Hope to see you again soon!
```
