# Zettel

Zettel is a **desktop Command Line Interface (CLI) application** for managing a personal Zettelkasten note-taking system. It's optimized for users who prefer keyboard commands, allowing you to efficiently build and navigate a knowledge base through your keyboard.

## Features

- **Multiple repositories**: Create and switch between different note repositories
- **Note management**: Create, edit, delete, and search notes with unique IDs
- **Organization**: Pin important notes, archive old ones, and tag notes for categorization
- **Linking**: Create one-way or bidirectional links between notes to build a knowledge graph
- **Tags**: Global tag management with rename and delete operations
- **Search**: Find notes by content or metadata

## Quick Start

### Prerequisites
- **JDK 17** (use the exact version)
- Gradle (included via wrapper)

### Setup in IntelliJ IDEA

1. **Ensure JDK 17 is configured** as described [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk)
2. **Import the project as a Gradle project** as described [here](https://se-education.org/guides/tutorials/intellijImportGradleProject.html)
3. **Verify setup**: Locate `src/main/java/seedu/zettel/Zettel.java`, right-click and choose `Run Zettel.main()`

### Running the Application

Build and run using Gradle:

```sh
./gradlew build
./gradlew run
```

Or on Windows:

```sh
gradlew.bat build
gradlew.bat run
```

## Usage Examples

```sh
# Initialize a new repository
init my_notes

# Create a note
new -t "First Note" -b "This is my first note"

# List all notes
list

# Pin a note
pin a1b2c3d4

# Add tags
new-tag important
add-tag a1b2c3d4 important

# Link notes together
link a1b2c3d4 e5f6g7h8

# Search for notes
find "keyword"
```

For detailed command documentation, see the [User Guide](docs/UserGuide.md).

**Note:** Keep the `src\main\java` folder as the root folder for Java files, as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Testing

### JUnit Tests

Run automated tests:

```sh
./gradlew test
```

Or on Windows:

```sh
gradlew.bat test
```

### Text UI Tests

Run I/O redirection tests:

```sh
cd text-ui-test
./runtest.sh        # Linux/Mac
runtest.bat         # Windows
```

## Code Quality

This project uses:
- **Gradle** for build automation and dependency management (see `build.gradle`)
- **Checkstyle** for code style enforcement (configuration in `config/checkstyle/`)
- **GitHub Actions** for continuous integration

Run Checkstyle locally:

```sh
./gradlew checkstyleMain checkstyleTest
```

If you are new to these tools:
- [Gradle Tutorial](https://se-education.org/guides/tutorials/gradle.html)
- [JUnit Tutorial](https://se-education.org/guides/tutorials/junit.html)
- [Checkstyle Tutorial](https://se-education.org/guides/tutorials/checkstyle.html)

## Documentation

Full documentation is available in the [`docs/`](docs/) folder:

- [User Guide](docs/UserGuide.md) - Complete command reference and usage instructions
- [Developer Guide](docs/DeveloperGuide.md) - Architecture and implementation details
- [About Us](docs/AboutUs.md) - Team information

## Project Structure

```
src/main/java/seedu/zettel/
├── commands/          # All command implementations
├── storage/           # File I/O and repository management
├── exceptions/        # Custom exceptions
├── util/              # Utility classes
├── Note.java          # Note data structure
├── UI.java            # User interface handling
├── Parser.java        # Command parsing
└── Zettel.java        # Main application class
```

## Contributing

See [CONTRIBUTORS.md](CONTRIBUTORS.md) for the list of contributors.

When contributing:
1. Follow the existing code style (enforced by Checkstyle)
2. Add tests for new features
3. Run `./gradlew check` before submitting PRs
4. Update relevant documentation

## Acknowledgments

This project is based on the Duke project template for CS2113 at the National University of Singapore.
