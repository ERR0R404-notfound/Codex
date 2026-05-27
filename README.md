# Codex

A lightweight desktop note-taking and document organization application built with Java Swing. Codex provides an intuitive interface for creating, organizing, and managing notes with support for sections, tags, and attachments.

## Features

- **Section Tree Navigation** — Organize notes hierarchically in a tree structure on the left sidebar
- **Rich Note Editor** — Full-featured text editor for composing and editing notes with live word count
- **Document Viewer** — View and render documents in multiple formats
- **Tag System** — Categorize and quickly find notes using tags
- **Attachments** — Support for attaching files to notes
- **Status Bar** — Real-time status information including word count and document statistics
- **Toolbar** — Quick access to common actions and operations
- **System Theme Integration** — Uses your system's native look and feel for seamless integration

## Project Structure

```
src/main/java/com/codex/
├── Main.java                    # Application entry point
├── ui/
│   ├── MainFrame.java           # Root application window
│   ├── SectionTree.java         # Hierarchical note organization panel
│   ├── NoteEditor.java          # Rich text editing panel
│   ├── DocumentViewer.java      # Document rendering and viewing
│   ├── Toolbar.java             # Top toolbar with common actions
│   └── StatusBar.java           # Bottom status information panel
├── model/
│   ├── Section.java             # Note section/folder model
│   ├── Note.java                # Individual note model
│   ├── Tag.java                 # Tag model for categorization
│   └── Attachment.java          # File attachment model
└── util/
    └── UITheme.java             # Theme and UI configuration constants
```

## Getting Started

Build and run Codex:

```bash
# Compile
javac -d bin src/main/java/com/codex/**/*.java

# Run
java -cp bin com.codex.Main
```

## Architecture

Codex uses a clean separation between UI and model layers:
- **UI Layer** — Swing components for rendering and user interaction
- **Model Layer** — Data structures representing notes, sections, tags, and attachments
- **Utility Layer** — Shared theme and UI configuration

The main window uses a BorderLayout with a split pane for the two-column layout: section tree on the left and note editor on the right.