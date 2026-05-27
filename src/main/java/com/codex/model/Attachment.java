package com.codex.model;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Attachment — represents a file attached to a note.
//  Supports PDF and DOCX file types.
// ─────────────────────────────────────────────────────────────────────────────

public class Attachment {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private String id;          // Firebase-generated key
    private String noteId;      // Parent note ID
    private String fileName;    // Display name e.g. "Lab1.pdf"
    private String filePath;    // Absolute local path to the file
    private String fileType;    // "pdf" or "docx"
    private long   createdAt;   // Unix timestamp

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTORS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Default constructor — required by Firebase for deserialization.
     */
    public Attachment() {}

    /**
     * Convenience constructor for creating a new attachment.
     */
    public Attachment(String noteId, String fileName, String filePath, String fileType) {
        this.noteId    = noteId;
        this.fileName  = fileName;
        this.filePath  = filePath;
        this.fileType  = fileType;
        this.createdAt = System.currentTimeMillis();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public String getId()        { return id; }
    public String getNoteId()    { return noteId; }
    public String getFileName()  { return fileName; }
    public String getFilePath()  { return filePath; }
    public String getFileType()  { return fileType; }
    public long   getCreatedAt() { return createdAt; }

    // ─────────────────────────────────────────────────────────────────────────
    //  SETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public void setId(String id)               { this.id = id; }
    public void setNoteId(String noteId)       { this.noteId = noteId; }
    public void setFileName(String fileName)   { this.fileName = fileName; }
    public void setFilePath(String filePath)   { this.filePath = filePath; }
    public void setFileType(String fileType)   { this.fileType = fileType; }
    public void setCreatedAt(long createdAt)   { this.createdAt = createdAt; }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns true if this attachment is a PDF file. */
    public boolean isPDF() {
        return "pdf".equalsIgnoreCase(fileType);
    }

    /** Returns true if this attachment is a DOCX file. */
    public boolean isDocx() {
        return "docx".equalsIgnoreCase(fileType);
    }

    @Override
    public String toString() {
        return "Attachment{id='" + id + "', fileName='" + fileName + "', type='" + fileType + "'}";
    }

}