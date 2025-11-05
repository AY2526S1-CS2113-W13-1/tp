# **Cheong Jian Hao's Project Portfolio Page**

# Project: ZettelCLI

ZettelCLI is a desktop Command Line Interface (CLI) application used for managing a personal Zettlekasten note system. It is optimised for users who prefer keyboard commands over Graphical User Interfaces (GUI). Through just a keyboard, users can build their own repository of notes or quickly navigate through their knowledge base with ease.

---

## 🧩 New Features

### **1. Architectural groundwork**
**What it does:**  
Conceptualized and implemented the **core skeletal structure** of the project, forming the foundation for all subsequent features.

**Details:**
- Designed the main class interaction model.  
- Established key architectural components — `Main`, `Parser`, `UI`, `Storage`, and `Command` — ensuring a clear separation of concerns.  
- Guided early-stage refactoring decisions and helped define coding and naming conventions.

**Justification:**  
Provided a strong, modular foundation for rest of team to work upon.

---

### **2. Parser system**
**What it does:**  
Reads user input and translates it into executable `Command` objects.

**Highlights:**
- Built the `Parser` class.
- Added robust error handling and validation for malformed or invalid input.  
- Integrated seamlessly with the `Command` class hierarchy for consistent behaviour.  
- Designed with extensibility in mind to support future command additions.

**Justification:**  
As a CLI-based application, accurate parsing and command handling are essential to usability and long-term maintainability.

### **3. ChangeRepo and CurrentRepo commands**
**What it does:**  
- `ChangeRepoCommand` — allows the user to switch the active note repository to another valid directory.
- `CurrentRepoCommand` — displays the currently active repository path.

**Highlights:**
- Implemented both commands and integrated them into the parser for command recognition.  
- Added validation and error messages for invalid or non-existent repository paths.  
- Worked with the `Storage` and `FileManager` modules to ensure repository state updates were handled safely.  
- Strengthened modularity by ensuring repository management was encapsulated cleanly within command logic.

**Justification:**  
These commands improve usability by providing flexibility in managing multiple note repositories. Users can easily switch between different workspaces or projects without restarting the application.

---

## ⚙️ Enhancements & Other Contributions

### **1. Storage module refactor and persistence enhancements**
**What it does:**  
Co-developed and later refactored the `Storage` component into three distinct classes for improved modularity and maintainability:
- `NoteSerializer` — handles the transformation of note objects to and from the file format.  
- `FileManager` — manages file I/O operations and ensures safe read/write behaviour.  
- `Storage` — orchestrates the above components to manage the overall repository state.

**Justification:**  
The refactor and persistence improvements ensured that note data, links, and deletions were consistently saved across sessions. This strengthened the reliability of the application and improved user trust in data integrity.

**Highlights:**  
- Enhanced multiple command implementations (e.g. pin, link, delete) to ensure proper synchronization with the storage layer.  
- Introduced more robust serialization logic to prevent partial or inconsistent saves.  
- Worked closely with teammates to update how storage interacted with core components after the refactor.  
- Improved clarity, testability, and maintainability across the persistence layer.

---

### **6. Documentation**
**What I did:**  
Compiled and structured detailed **feature behavior documentation**, outlining command usage, expected outputs, and edge cases.

**Highlights:**
- Authored comprehensive **manual testing section** in the Developer Guide, covering:
  - Note creation and editing workflows (`new`, `edit`, `delete`)
  - Listing, searching, and filtering (`list`, `list -p`, `list -a`, `find`)
  - Linking features (`link`, `link-both`, incoming/outgoing link listings)
  - Tag management (`new-tag`, `add-tag`, `delete-tag`, `rename-tag`)
  - Error and edge-case handling (invalid IDs, orphaned files, corrupted data)
  - Storage and recovery validation (corruption handling, file recreation)
- Ensured that each test case clearly specifies the **command**, **expected result**, and **rationale**.
- Verified persistence correctness after storage refactor to confirm data consistency across sessions.
- Collaborated with teammates to align UG and DG documentation formats for consistency and clarity.

---

### **3. Project management & collaboration**
- Regularly reviewed pull requests with non-trivial feedback.  
- Advocated for modular design patterns and consistent coding conventions.  
- Coordinated system-wide refactors and team-wide testing sessions.
- Spotted bugs and potential pitfalls in team code, gave according feedback or made adjustments myself.

---

### **4. Community involvement**
- Reviewed peers’ PRs and provided feedback on design and maintainability.  
- Contributed to team discussions on architecture, code quality, and feature direction.  
- Assisted teammates in understanding core design decisions and code conventions.

---

### **5. Tools & technologies**
- **Language:** Java 17 — utilized java.nio for file I/O operations and java.time.Instant for consistent and timezone-independent timestamp management.
- **Testing:** JUnit 5 (Jupiter) — implemented @Test-based unit and integration tests, including exception and boundary testing for the Command and Storage modules.
- **Version Control:** Git & GitHub (issues, PRs, branching)  

---

### **Code contributed**
See my contributions via RepoSense:  
[RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=JianHao24&breakdown=true)

---

### **Credits**
Inspired by the CS2113 teaching template and AddressBook-Level3.  
All main features and design were implemented by the ZettelCLI team.  
No third-party dependencies were used beyond the Java SDK.

---

_Last updated: October 2025_

