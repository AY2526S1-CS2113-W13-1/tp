# Developer Guide

## Acknowledgements

* **Zettelkasten Method** - Note-taking methodology inspired by Niklas Luhmann's slip-box system
  * [Zettelkasten.de](https://zettelkasten.de/) - Introduction to the Zettelkasten method
* **Java NIO File API** - Used for file system operations and storage management
  * [Oracle Java Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/package-summary.html)
* **Instant class (java.time)** - Used for timestamp management
  * [Oracle Java Time API](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)

## Design & implementation

### Architecture Overview

ZettelCLI follows a layered architecture with clear separation of concerns:
```
┌─────────────────┐
│  CLI Interface  │  (User interaction layer - UI)
└────────┬────────┘
         │
┌────────▼────────┐
│     Parser      │  (Input parsing layer)
└────────┬────────┘
         │
┌────────▼────────┐
│    Commands     │  (Business logic layer)
└────────┬────────┘
         │
┌────────▼────────┐
│     Storage     │  (Data persistence layer)
└─────────────────┘
         │
┌────────▼────────┐
│      Notes      │  (Data model layer)
└─────────────────┘
```

### Main Application Component (Zettel)

The `Zettel` class serves as the main entry point and orchestrator for the entire application.

**Key Responsibilities:**
- Initialize all components (UI, Storage, Parser)
- Load existing notes from disk on startup
- Run the main command loop
- Handle graceful shutdown and error recovery
- Auto-save notes after each command

**Main Application Flow:**
```
Start → Initialize Storage → Load Notes → Show Welcome
  ↓
Enter Command Loop:
  ├─ Read User Input (with timeout)
  ├─ Parse Command
  ├─ Execute Command
  ├─ Auto-save Notes
  └─ Loop until Exit
  ↓
Cleanup → Shutdown → Exit
```

**Class Diagram:**
```
┌─────────────────────────┐
│        Zettel           │
├─────────────────────────┤
│ - storage: Storage      │
│ - notes: ArrayList      │
│ - ui: UI                │
│ - isRunning: boolean    │
├─────────────────────────┤
│ + run(): void           │
│ + main(String[]): void  │
└─────────────────────────┘
```

**Key Implementation Details:**

The application uses an ExecutorService with timeout handling to prevent hanging in CI environments:
```java
Future<String> future = executor.submit(() -> ui.readCommand());
String userInput = future.get(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);
```

Auto-save is implemented after every successful command execution:
```java
command.execute(notes, ui, storage);
storage.save(notes);  // Auto-save after each operation
```

### Parser Component

The `Parser` class converts raw user input strings into executable `Command` objects.

**Key Responsibilities:**
- Tokenize and validate user input
- Extract command parameters
- Validate parameter formats (note IDs, repo names, etc.)
- Create appropriate Command objects
- Throw descriptive exceptions for invalid input

**Supported Commands:**
- `bye` - Exit the application
- `list [-p]` - List all notes or pinned notes only
- `new -t <TITLE> [-b <BODY>]` - Create a new note
- `delete [-f] <NOTE_ID>` - Delete a note with optional force flag
- `pin <NOTE_ID>` - Pin a note
- `unpin <NOTE_ID>` - Unpin a note
- `init <REPO_NAME>` - Initialize a new repository
- `find <SEARCH_TERM>` - Search for notes

**Validation Rules:**

Note ID validation:
- Must be exactly 8 characters long
- Must contain only lowercase hexadecimal characters (0-9, a-f)
- Regex pattern: `^[a-f0-9]{8}$`

Repository name validation:
- Must contain only alphanumeric characters, hyphens, and underscores
- Regex pattern: `[a-zA-Z0-9_-]+`

**Parsing Flow Example (New Note Command):**
```
Input: "new -t My Note -b Some content"
  ↓
Split by spaces: ["new", "-t", "My", "Note", "-b", "Some", "content"]
  ↓
Find -t flag index → Extract title: "My Note"
Find -b flag index → Extract body: "Some content"
  ↓
Validate title not empty
  ↓
Create NewNoteCommand(title, body)
```

**Error Handling Strategy:**

The Parser throws three types of exceptions:
- `EmptyDescriptionException` - Required parameters are missing
- `InvalidFormatException` - Command format is incorrect
- `InvalidInputException` - Unrecognized command or invalid values

### UI Component

The `UI` class handles all user interaction, including input reading and output display.

**Key Responsibilities:**
- Display welcome message and command help
- Read user commands from standard input
- Display note lists and search results
- Show confirmation prompts
- Display error messages
- Handle graceful shutdown

**Key Methods:**

Input Methods:
- `readCommand()` - Reads a line of user input with EOF handling

Output Methods:
- `showWelcome()` - Displays greeting and available commands
- `showNoteList(List<Note>, boolean)` - Displays formatted note list
- `showAddedNote(Note)` - Confirms note creation
- `showDeleteConfirmation(String, String)` - Prompts for delete confirmation
- `showError(String)` - Displays error messages
- `showBye()` - Displays farewell message

**Display Formats:**

Note list format:
```
You have 3 notes:
    1. my_note.txt 2025-10-14 a1b2c3d4
    2. ideas.txt 2025-10-13 b2c3d4e5
    3. todo.txt 2025-10-12 c3d4e5f6
```

Welcome message includes command reference for easy access.

### Command Component

The `Command` abstract class defines the interface for all executable commands using the Command Pattern.

**Class Hierarchy:**
```
                          Command
                         (abstract)
                             │
     ┌───────┬───────┬───────┬───────┬───────┬───────┐
     │       │       │       │       │       │       │
    New    List   Delete    Pin     Find    Init    Exit
                         (concrete)
```

**Available Commands:**

1. **NewNoteCommand** - Creates a new note
   - Generates 8-character hash-based ID from title + timestamp
   - Validates filename uniqueness
   - Supports title and optional body

2. **ListNoteCommand** - Lists notes
   - Sorts by creation date (newest first)
   - Optional filter for pinned notes only
   - Handles empty list scenarios

3. **DeleteNoteCommand** - Deletes a note
   - Validates note existence
   - Optional force flag to skip confirmation
   - Prompts user for confirmation by default

4. **PinNoteCommand** - Pins or unpins a note
   - Validates note ID format
   - Updates pinned status
   - Supports both pin and unpin operations

5. **FindNoteCommand** - Searches notes
   - Case-insensitive search
   - Searches in note body content
   - Returns all matching notes

6. **InitCommand** - Initializes a repository
   - Validates repository name format
   - Creates repository structure
   - Displays confirmation message

7. **ExitCommand** - Exits the application
   - Displays farewell message
   - Returns true for `isExit()` check

**Command Execution Pattern:**

Each command follows this pattern:
1. **Validation** - Check preconditions (empty list, invalid IDs, etc.)
2. **Business Logic** - Perform the actual operation
3. **UI Feedback** - Display success/failure messages
4. **Persistence** - Save is handled by Zettel class after execution

**Sequence Diagram - Execute Command:**
```
User → Zettel → Parser → Command → Notes/Storage → UI
 |       |        |         |           |          |
 | input |        |         |           |          |
 |------>|        |         |           |          |
 |       | parse()|         |           |          |
 |       |------->|         |           |          |
 |       |<-------|         |           |          |
 |       |                  |           |          |
 |       |  cmd execute()   |           |          |
 |       |----------------->|           |          |
 |       |                  |  modify   |          |
 |       |                  |---------->|          |
 |       |                  |           |   show   |
 |       |                  |--------------------->|
 |       |      save()      |           |          |
 |       |----------------------------->|          |
```

### Note Component

The `Note` class represents a single note entity with all its metadata and content.

**Key Fields:**
- `id` (String) - Unique 8-character hash-based identifier
- `title` (String) - Note title
- `filename` (String) - Associated file name for storage
- `body` (String) - Note content
- `createdAt` (Instant) - Creation timestamp
- `modifiedAt` (Instant) - Last modification timestamp
- `pinned` (boolean) - Flag for quick access
- `archived` (boolean) - Flag for archival status
- `archiveName` (String) - Archive location (if archived)
- `logs` (List<String>) - List of timestamped log entries

**Class Diagram:**
```
┌─────────────────────────────────┐
│            Note                 │
├─────────────────────────────────┤
│ - id: String (8 chars)          │
│ - title: String                 │
│ - filename: String              │
│ - body: String                  │
│ - createdAt: Instant            │
│ - modifiedAt: Instant           │
│ - pinned: boolean               │
│ - archived: boolean             │
│ - archiveName: String           │
│ - logs: List<String>            │
│ - numberOfNotes: int (static)   │
├─────────────────────────────────┤
│ + Note(id, title, ...)          │
│ + getId(): String               │
│ + getTitle(): String            │
│ + setTitle(String): void        │
│ + setPinned(boolean): void      │
│ + addLog(String): void          │
│ + updateModifiedAt(): void      │
│ + toString(): String            │
└─────────────────────────────────┘
```

**Constructor Overloading:**

Two constructors support different use cases:

1. **User Creation Constructor** - For new notes created by users
```java
   Note(id, title, filename, body, createdAt, modifiedAt)
   // Defaults: pinned=false, archived=false, logs=[]
```

2. **Storage Loading Constructor** - For loading notes from disk
```java
   Note(id, title, filename, body, createdAt, modifiedAt, 
        pinned, archived, archiveName, logs)
   // Includes all fields for complete restoration
```

**ID Generation:**

Note IDs are deterministic hash-based identifiers:
- Generated from: `title + createdAt timestamp`
- Hashed to produce 8-character hexadecimal string
- Ensures uniqueness while being reproducible

**Automatic Timestamp Management:**

The `updateModifiedAt()` method is automatically called by setters that modify content:
- `setTitle()`
- `setBody()`
- `setPinned()`
- `setArchived()`
- `setArchiveName()`

This ensures modification timestamps are always accurate without manual tracking.

**Display Format:**

The `toString()` method provides a formatted display for note lists:
```
Format: FILENAME yyyy-MM-dd NOTEID
Example: my_note.txt 2025-10-14 a1b2c3d4
```

**Defensive Copying:**

The `getLogs()` method returns a defensive copy to maintain encapsulation:
```java
public List<String> getLogs() {
    return new ArrayList<>(logs);
}
```

**Static Counter:**

A static `numberOfNotes` counter tracks total notes created during application runtime for statistics.

### Storage Component

The `Storage` class manages all file I/O operations and repository management.

**Key Responsibilities:**
- Initialize and manage repository structure
- Save and load notes from disk
- Handle multiple repositories
- Maintain configuration state

**Repository Structure:**
```
<root>/
├── data/                 # Root data directory
│   ├── main/             # Default repository
│   │   ├── notes/        # Active notes folder
│   │   ├── archive/      # Archived notes folder
│   │   └── index.json    # Repository index
│   └── .zettelConfig     # Active repository config
├── commands/             # Command classes
│   ├── Command           # Abstract base class
│   ├── NewNoteCommand    # Create new note
│   ├── ListNoteCommand   # List notes
│   ├── DeleteNoteCommand # Delete note
│   ├── PinNoteCommand    # Pin/unpin note
│   ├── FindNoteCommand   # Search notes
│   └── InitCommand       # Init repository
├── exceptions/           # Custom exceptions
├── Note                  # Note model
├── Parser                # Input parser
├── Storage               # File handling
├── UI                    # User interface
└── Zettel (main)         # Main entry point
```

**Class Diagram:**
```
┌─────────────────────────┐
│       Storage           │
├─────────────────────────┤
│ - rootPath: Path        │
│ - repoName: String      │
├─────────────────────────┤
│ + init(): void          │
│ + load(): ArrayList     │
│ + save(List): void      │
│ + createRepo(String)    │
│ + changeRepo(String)    │
└─────────────────────────┘
```

**Save Format:**

Each note is stored as a single line with pipe-delimited fields:
```
id | title | filename | body | createdAt | modifiedAt | pinned | archived | archiveName | logs
```

Special handling:
- Newlines in body are escaped as `\\n`
- Multiple logs are separated by `;;`
- Empty archiveName stored as empty string

**Sequence Diagram - Saving Notes:**
```
Zettel  →  Storage
  |            |
  |            |
  |            |
  |save(notes) |
  |----------->|
  |            | toSaveFormat()
  |            | Files.write()
  |<-----------|
  |            |
```

### Repository Management

**Creating a Repository:**
1. Check if repository already exists
2. Create directory structure (`notes/`, `archive/`)
3. Create `index.txt` file
4. Handle errors gracefully

**Switching Repositories:**
1. Update internal `repoName` state
2. Write new repository name to `.zettelConfig`
3. Subsequent operations use new repository

**Code Snippet:**
```java
public void changeRepo(String repoName) {
    try {
        this.repoName = repoName;
        Path configPath = rootPath.resolve(CONFIG_FILE);
        Files.writeString(configPath, repoName);
    } catch (IOException e) {
        System.out.println("Unable to change repository to " + repoName);
    }
}
```

### Error Handling Strategy

The application follows a fail-safe approach:
- IOExceptions are caught and logged with user-friendly messages
- Corrupted note lines are skipped during parsing
- Missing directories are created automatically during initialization
- Returns empty collections rather than null on load failures

### Data Persistence Flow

**Loading Notes:**
1. Resolve repository path from `repoName`
2. Read `zettel.txt` line by line
3. Parse each line with `parseSaveFile()`
4. Filter out null entries (corrupted lines)
5. Collect into ArrayList

**Saving Notes:**
1. Ensure repository directory exists
2. Convert each Note to save format string
3. Write all lines atomically to file
4. Overwrite previous content completely

### Component Interaction Diagram
```
┌────────────────────────────────────────────────────┐
│                      ZettelCLI                     │ 
│                                                    │
│  ┌──────┐  ┌──────┐  ┌───────┐  ┌───────┐  ┌────┐  │
│  │Zettel│  │Parser│  │Command│  │Storage│  │ UI │  │
│  └──┬───┘  └──┬───┘  └───┬───┘  └───┬───┘  └─┬──┘  │
│     │         │          │   load   │        │     │
│     │<──────────────────────────────│        │     │
│     │         │          │          │  scan  │     │
│     │<───────────────────────────────────────│     │
│     │  parse  │          │          │        │     │
│     │─────────>          │          │        │     │
│     │<────────│          │          │        │     │
│     │    cmd execute     │          │        │     │
│     │────────────────────>          │        │     │
│     │         │  modify  │          │        │     │
│     │         │  notes   │          │        │     │
│     │<───────────────────│          │        │     │
│     │         │          │          │  show  │     │
│     │         │          │──────────────────>│     │
│     │         │   save   │          │        │     │
│     │───────────────────────────────>        │     │
│     │         │          │          │        │     │
└────────────────────────────────────────────────────┘
```

## Product scope

### Target user profile

**Primary User:** Students, researchers, knowledge workers, and lifelong learners who:
- Work primarily in terminal/command-line environments
- Value speed and efficiency over graphical interfaces
- Need to capture ideas quickly without context-switching
- Want an offline-first solution without cloud dependencies
- Prefer minimalist, keyboard-driven workflows
- Work on systems with limited resources (old computers, minimal setups)
- Need to take notes in low-connectivity environments (rural areas, during commutes)
- Follow or want to adopt the Zettelkasten note-taking methodology

**Technical Profile:**
- Comfortable with CLI applications
- Basic understanding of file systems
- May have older hardware or limited system resources

### Value proposition

ZettelCLI solves the problem of **slow, bloated, cloud-dependent note-taking applications** by providing:

1. **Speed & Efficiency**: Capture notes instantly from the command line without launching heavy applications
2. **Offline-First**: No internet required - all notes stored locally
3. **Resource-Light**: Runs on old computers and minimal systems
4. **Zero Lock-In**: Plain text storage means your notes are never trapped
5. **Zettelkasten Support**: Built-in features for linking notes and building knowledge networks
6. **Privacy**: Your notes never leave your machine
7. **Distraction-Free**: No ads, notifications, or unnecessary features
8. **Always Accessible**: Available wherever you have a terminal

**Core Value Statements:**
- "Take notes as fast as you can think"
- "Your notes, your computer, no internet required"
- "Zettelkasten for the terminal generation"

## User Stories

| Version | As a ...                  | I want to ...                                              | So that I can ...                                          |
|---------|---------------------------|------------------------------------------------------------|------------------------------------------------------------|
| v1.0    | impatient user            | take notes from my local CLI                               | have an offline and quick note-taking experience           |
| v1.0    | hurried user              | create a note with only a title                            | flesh it out later                                         |
| v1.0    | minimalist user           | create notes without specifying a directory                | keep my note tree flat and uncluttered                     |
| v1.0    | distracted user           | save a half-written note as a draft                        | not lose it                                                |
| v1.0    | nostalgic user            | view the creation date of notes                            | trace when an idea originated                              |
| v1.0    | cautious user             | view a confirmation before deleting a note                 | not lose information accidentally                          |
| v1.0    | user                      | search notes by title                                      | find them quickly                                          |
| v1.0    | time-tracker              | add a timestamped log entry to a note                      | see when I last thought about it                           |
| v1.0    | new user                  | initialize a Zettelkasten repo                             | start with a clean slate                                   |
| v1.0    | returning user            | import a repo into my new computer                         | not lose momentum                                          |
| v1.0    | heavy user                | archive old notes                                          | they don't clutter my active space                         |
| v1.0    | forgetful user            | mark notes as "pinned"                                     | always find them                                           |
| v1.0    | busy student              | create a short note quickly                                | keep up with notetaking during lectures                    |
| v2.0    | lazy user                 | tag notes under categories                                 | retrieve them easily                                       |
| v2.0    | multi-disciplinary user   | assign multiple tags to a note                             | it appears under different contexts                        |
| v2.0    | tidy user                 | see a list of all my tags                                  | spot duplicates or overlaps                                |
| v2.0    | user                      | sort results by creation date                              | trace my thought evolution                                 |
| v2.0    | forgetful user            | search notes case-insensitively                            | not have to remember names perfectly                       |
| v2.0    | user                      | edit existing notes                                        | update and refine my thoughts                              |
| v3.0    | user                      | link one note to another                                   | build a web of ideas                                       |
| v3.0    | future-focused user       | create a placeholder link to a note that doesn't exist yet | fill it in later                                           |
| v3.0    | user revising connections | unlink notes                                               | correct outdated associations                              |
| v3.0    | busy student              | access linked notes                                        | study immediately related notes                            |
| v3.0    | organised student         | link notes together                                        | not require much effort to organise them                   |
| v4.0    | user                      | fuzzy search within content                                | retrieve notes even if I don't remember exact wording      |
| v4.0    | tag-oriented user         | filter search results by tag                               | narrow scope                                               |
| v4.0    | speed-focused user        | navigate search results with keyboard shortcuts            | stay efficient                                             |
| v4.0    | pragmatic user            | rename a tag globally                                      | all affected notes update consistently                     |
| v4.0    | careless typist           | be warned if I create two tags with similar spellings      | not fragment my notes                                      |
| v4.0    | visual learner            | generate a graph of linked notes                           | visualise the relationships                                |

## Non-Functional Requirements

### Performance
1. **Response Time**: Commands should execute in under 200ms for typical operations
2. **Startup Time**: Application should initialize in under 1 second
3. **Search Speed**: Search through 1000+ notes should complete within 500ms
4. **File Size**: Should handle repositories with 10,000+ notes efficiently

### Usability
1. **Learning Curve**: New users should be able to create their first note within 2 minutes
2. **Error Messages**: All error messages should be clear and actionable
3. **Command Format**: Commands should follow consistent CLI conventions
4. **Help Documentation**: Built-in help should be available for all commands

### Reliability
1. **Data Integrity**: No data loss should occur during normal operations
2. **Graceful Degradation**: Corrupted notes should be skipped, not crash the application
3. **Atomic Operations**: Save operations should be atomic to prevent partial writes
4. **Backup Safety**: Original data should never be corrupted during updates

### Portability
1. **Cross-Platform**: Should run on Windows, macOS, and Linux
2. **Java Version**: Compatible with Java 17 and above
3. **File Format**: Use platform-independent text encoding (UTF-8)
4. **Path Handling**: Use platform-independent path separators

### Security
1. **Local Storage**: All data stored locally, no network transmission
2. **File Permissions**: Respect system file permissions
3. **Input Validation**: Sanitize user input to prevent file system exploits

### Maintainability
1. **Code Style**: Follow Java coding conventions
2. **Documentation**: All public methods should have Javadoc comments
3. **Modularity**: Clear separation between UI, logic, and storage layers
4. **Testability**: Code should be designed for unit testing

### Scalability
1. **Storage**: Should handle repositories up to 100MB efficiently
2. **Memory**: Should run with JVM heap size under 512MB
3. **Concurrent Use**: Support multiple repository instances (different directories)

### Resource Constraints
1. **Disk Space**: Minimal installation footprint (< 10MB)
2. **CPU Usage**: Low CPU usage during idle state
3. **Memory**: Should run on systems with 2GB RAM
4. **Old Hardware**: Must be usable on 5+ year old computers

## Glossary

* **Zettelkasten** - A German term meaning "slip box"; a knowledge management and note-taking method based on creating atomic notes and linking them together
* **Repository (Repo)** - A collection of notes stored in a dedicated folder structure with its own notes, archive, and index
* **Note** - A single unit of information with a title, body, metadata, and optional tags/links
* **Note ID** - An 8-character hexadecimal identifier uniquely identifying a note, generated deterministically from title and timestamp
* **Pinned Note** - A note marked for quick access that appears at the top of lists
* **Archived Note** - A note moved to the archive folder to reduce clutter in the active workspace
* **Draft** - A partially written note that has been saved for later completion
* **Log Entry** - A timestamped record added to a note to track when it was viewed or modified
* **Atomic Note** - A note that captures a single idea or concept (Zettelkasten principle)
* **Backlink** - A reverse link showing which notes link to the current note
* **Note Link** - A connection between two notes that represents a relationship or reference
* **Placeholder Link** - A link to a note that doesn't exist yet, indicating future content to create
* **Fuzzy Search** - A search method that finds approximate matches, allowing for typos and variations
* **CLI** - Command Line Interface; a text-based interface for interacting with the application
* **Root Path** - The base directory where all ZettelCLI repositories are stored (default: `data/`)
* **Config File** - `.zettelConfig` file that stores the current active repository name
* **Index File** - `index.txt` file within each repository for maintaining note metadata
* **Save Format** - The pipe-delimited text format used to persist notes to disk
* **Force Delete** - Deleting a note without confirmation prompt using the `-f` flag
* **Parser** - The component responsible for converting raw user input into Command objects
* **Command Pattern** - A design pattern that encapsulates requests as objects, used for all user actions
* **Auto-save** - Automatic saving of notes after every successful command execution
* **Defensive Copy** - Returning a copy of internal data structures to prevent external modification
