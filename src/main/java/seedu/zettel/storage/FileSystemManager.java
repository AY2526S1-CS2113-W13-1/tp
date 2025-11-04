package seedu.zettel.storage;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.zettel.exceptions.FailedMoveNoteException;
import seedu.zettel.exceptions.InvalidRepoException;
import seedu.zettel.exceptions.ZettelException;

/**
 * Manages file system operations for Zettel repositories.
 * Handles directory creation, validation, and file management.
 */
public class FileSystemManager {

    /** Configuration file name for storing repository settings. */
    static final String CONFIG_FILE = ".zettelConfig";

    /** File name for storing all tags globally. */
    static final String TAGS_FILE = "tags.txt";

    /** Directory name for storing note body files within a repository. */
    static final String REPO_NOTES = "notes";

    /** Directory name for storing archived notes within a repository. */
    static final String REPO_ARCHIVE = "archive";

    /** File name for the repository index that stores note metadata. */
    static final String REPO_INDEX = "index.txt";

    private final Path rootPath;

    /**
     * Constructs a FileSystemManager with the specified root path.
     *
     * @param rootPath the root directory path for all repositories
     */
    public FileSystemManager(String rootPath) {
        this.rootPath = Paths.get(rootPath);
    }

    /**
     * Gets the root path of the file system manager.
     *
     * @return the root directory path
     */
    public Path getRootPath() {
        return rootPath;
    }

    /**
     * Creates the root folder if it doesn't exist.
     */
    public void createRootFolder() {
        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + rootPath + " folder.");
        }
    }

    /**
     * Creates the configuration file with repository settings if it doesn't exist.
     * If the config file is missing, attempts to reconstruct it from existing repositories.
     *
     * Config file format:
     * Line 1: Current repository name
     * Line 2: Default repository name
     *
     * @param defaultRepo the default repository name to use if no repositories exist
     */
    public void createConfigFile(String defaultRepo) {
        Path configPath = rootPath.resolve(CONFIG_FILE);
        try {
            if (Files.notExists(configPath)) {
                Files.createFile(configPath);

                List<String> existingRepos = findExistingRepositories();

                String currentRepo;
                String defaultRepoToUse;

                if (!existingRepos.isEmpty()) {
                    // Use first found repository as both current and default
                    currentRepo = existingRepos.get(0);
                    defaultRepoToUse = existingRepos.get(0);
                    System.out.println("Reconstructed config from existing repositories.");
                    System.out.println("Found repositories: " + String.join(", ", existingRepos));
                    System.out.println("Set '" + currentRepo + "' as current and default repository.");
                } else {
                    // No existing repos found, use provided default
                    currentRepo = defaultRepo;
                    defaultRepoToUse = defaultRepo;
                }

                List<String> lines = List.of(currentRepo, defaultRepoToUse);
                Files.write(configPath, lines);
            }
        } catch (IOException e) {
            System.out.println("Error creating " + CONFIG_FILE + ".");
        }
    }

    /**
     * Scans the root directory to find existing repository folders.
     * A valid repository must contain the required subdirectories and index file.
     *
     * @return list of repository names found in the root directory
     */
    private List<String> findExistingRepositories() {
        List<String> repos = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && isValidRepository(entry)) {
                    repos.add(entry.getFileName().toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error scanning for existing repositories: " + e.getMessage());
        }

        return repos;
    }

    /**
     * Checks if a directory is a valid Zettel repository.
     * A valid repository must have a notes/ directory, archive/ directory, and index.txt file.
     *
     * @param repoPath the path to check
     * @return true if the directory is a valid repository
     */
    private boolean isValidRepository(Path repoPath) {
        Path notesDir = repoPath.resolve(REPO_NOTES);
        Path archiveDir = repoPath.resolve(REPO_ARCHIVE);
        Path indexFile = repoPath.resolve(REPO_INDEX);

        return Files.isDirectory(notesDir) &&
                Files.isDirectory(archiveDir) &&
                Files.isRegularFile(indexFile);
    }


    //@@author gordonajajar
    /**
     * Creates the directory structure for a new repository.
     *
     * @param repoName the name of the repository to create
     * @return true if the repository was created successfully, false if it already exists
     */
    public boolean createRepoStructure(String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        if (Files.exists(repoPath)) {
            System.out.println("Repository /" + repoName + " already exists.");
            return false;
        }
        try {
            Files.createDirectories(repoPath.resolve(REPO_NOTES));
            Files.createDirectories(repoPath.resolve(REPO_ARCHIVE));
            Files.createFile(repoPath.resolve(REPO_INDEX));
            return true;
        } catch (IOException e) {
            System.out.println("Error initialising repository " + repoName);
            return false;
        }
    }

    /**
     * Creates a note file with the specified content in the given repository.
     *
     * @param noteFilename the name of the note file to create
     * @param noteBody the content to write to the note file
     * @param repoName the repository where the note file should be created
     */
    public void createNoteFile(String noteFilename, String noteBody, String repoName) {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);

        try {
            Path noteFile = notesDir.resolve(noteFilename);
            if (Files.notExists(noteFile)) {
                Files.createFile(noteFile);
            } else {
                System.out.println("Note file already exists. Overwriting... " + noteFile);
            }

            Files.writeString(noteFile, noteBody != null ? noteBody : "");

        } catch (IOException e) {
            System.out.println("Error writing note file: " + e.getMessage());
        }
    }


    /**
     * Validates the structure of a repository and creates missing components.
     *
     * @param repoName the name of the repository to validate
     * @param expectedFilesMap map of expected filenames -> isArchived flag
     * @throws ZettelException if the repository structure is invalid
     */
    public void validateRepoStructure(String repoName, Map<String, Boolean> expectedFilesMap) throws ZettelException {
        Path repoPath = rootPath.resolve(repoName);
        Path notesDir = repoPath.resolve(REPO_NOTES);
        Path archiveDir = repoPath.resolve(REPO_ARCHIVE);
        Path indexFile = repoPath.resolve(REPO_INDEX);
        Path configFile = rootPath.resolve(CONFIG_FILE);

        if (!Files.exists(configFile)) {
            throw new InvalidRepoException(".zettelConfig missing at " + configFile.toAbsolutePath());
        }

        createIfMissing(repoPath, "repository folder: " + repoName, true);
        createIfMissing(notesDir, "notes/ for repo: " + repoName, true);
        createIfMissing(archiveDir, "archive/ for repo: " + repoName, true);
        createIfMissing(indexFile, "index.txt for repo: " + repoName, false);

        // Create missing body files in the correct directory (notes/ or archive/)
        for (Map.Entry<String, Boolean> entry : expectedFilesMap.entrySet()) {
            String fileName = entry.getKey();
            boolean isArchived = entry.getValue();
            Path bodyFile = isArchived ? archiveDir.resolve(fileName) : notesDir.resolve(fileName);
            try {
                createIfMissing(bodyFile, (isArchived ? "archive body file: " : "body file: ") + fileName, false);
            } catch (ZettelException e) {
                System.out.println("Warning: " + e.getMessage());
            }
        }

        // Build expected lists per directory for orphan detection
        List<String> expectedInNotes = expectedFilesMap.entrySet().stream()
                .filter(e -> !e.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> expectedInArchive = expectedFilesMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Detect orphans in both directories
        detectOrphans(notesDir, expectedInNotes, repoName);
        detectOrphans(archiveDir, expectedInArchive, repoName);
    }
    //@@author

    /**
     * Ensures that the global tags file exists in the root directory.
     * <p>
     * If the file already exists, the method returns {@code true}.
     * If the file does not exist, it will be created and the method returns {@code false}.
     * </p>
     *
     * @throws ZettelException if creating the tags file fails
     */
    public void validateTagsFile() throws ZettelException {
        Path tagsFile = rootPath.resolve(TAGS_FILE);
        try {
            if (Files.notExists(tagsFile)) {
                Files.createFile(tagsFile);
            }
        } catch (IOException e) {
            throw new ZettelException("Error creating tags file: " + e.getMessage());
        }
    }

    //@@author gordonajajar
    /**
     * Creates a file or directory if it doesn't exist.
     *
     * @param path the path to create
     * @param description a description of what is being created
     * @param isDirectory true if creating a directory, false for a file
     * @throws ZettelException if the creation fails
     */
    private void createIfMissing(Path path, String description, boolean isDirectory) throws ZettelException {
        try {
            if (Files.notExists(path)) {
                if (isDirectory) {
                    Files.createDirectories(path);
                } else {
                    Files.createFile(path);
                }
                System.out.println("Created missing " + description);
            }
        } catch (IOException e) {
            System.out.println("Failed to create " + description + ": " + e.getMessage());
        }
    }

    /**
     * Detects orphan files (files not referenced in the index) in the notes directory.
     *
     * @param notesDir the notes directory to scan
     * @param expectedFiles the list of expected files from the index
     * @param repoName the name of the repository being scanned
     * @throws ZettelException if scanning the directory fails
     */
    private void detectOrphans(Path notesDir, List<String> expectedFiles, String repoName) throws ZettelException {
        try (DirectoryStream<Path> notesStream = Files.newDirectoryStream(notesDir, "*.txt")) {
            List<String> orphans = new ArrayList<>();
            for (Path p : notesStream) {
                if (!expectedFiles.contains(p.getFileName().toString())) {
                    orphans.add(p.getFileName().toString());
                }
            }
            if (!orphans.isEmpty()) {
                System.out.println("Notice: Found " + orphans.size() +
                        " orphan note file(s) in repo '" + repoName + "':");
                orphans.forEach(f -> System.out.println("  - " + f));
            }
        } catch (IOException e) {
            System.out.println("Failed to scan notes directory for orphans: " + e.getMessage());
        }
    }
    //@@author

    /**
     * Gets the path to the index file for the specified repository.
     *
     * @param repoName the repository name
     * @return the path to the repository's index file
     */
    public Path getIndexPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_INDEX);
    }

    /**
     * Gets the path to the notes directory for the specified repository.
     *
     * @param repoName the repository name
     * @return the path to the repository's notes directory
     */
    public Path getNotesPath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_NOTES);
    }

    /**
     * Gets the path to the archive directory for the specified repository.
     *
     * @param repoName the repository name
     * @return the path to the repository's archive directory
     */
    public Path getArchivePath(String repoName) {
        return rootPath.resolve(repoName).resolve(REPO_ARCHIVE);
    }

    /**
     * Gets the path to the configuration file.
     *
     * @return the path to the configuration file
     */
    public Path getConfigPath() {
        return rootPath.resolve(CONFIG_FILE);
    }

    /**
     * Moves a note file between the notes and archive directories.
     *
     * @param filename the name of the note file to move
     * @param repoName the name of the repository containing the note
     * @param toArchive true to move to archive, false to move to notes
     * @throws ZettelException if the file move operation fails
     */
    public void moveNoteBetweenDirectories(String filename, String repoName, boolean toArchive)
            throws ZettelException {
        Path sourcePath = toArchive
                ? getNotesPath(repoName).resolve(filename)
                : getArchivePath(repoName).resolve(filename);
        Path destPath = toArchive
                ? getArchivePath(repoName).resolve(filename)
                : getNotesPath(repoName).resolve(filename);

        try {
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            String action = toArchive ? "archive" : "unarchive";
            throw new FailedMoveNoteException("Failed to " + action + " note file '" + filename + "': "
                    + e.getMessage());
        }
    }
}
