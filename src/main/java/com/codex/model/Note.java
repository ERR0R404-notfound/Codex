package com.codex.model;

// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.util.ArrayList;
import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Note — represents a single note in Codex.
//  Belongs to one Section. Can have multiple attachments and tags.
// ─────────────────────────────────────────────────────────────────────────────

public class Note {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private String       id;              // Firebase-generated key
    private String       title;           // Note title
    private String       content;         // Note body text
    private String       sectionId;       // Parent section ID
    private boolean      isPinned;        // Pinned to top of list
    private long         createdAt;       // Unix timestamp
    private long         updatedAt;       // Unix timestamp
    private List<String> tagIds;          // IDs of tags applied to this note
    private List<String> attachmentIds;   // IDs of attachments on this note

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTORS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Default constructor — required by Firebase for deserialization.
     */
    public Note() {
        this.tagIds        = new ArrayList<>();
        this.attachmentIds = new ArrayList<>();
    }

    /**
     * Convenience constructor for creating a new note.
     */
    public Note(String title, String content, String sectionId) {
        this();
        this.title     = title;
        this.content   = content;
        this.sectionId = sectionId;
        this.isPinned  = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public String       getId()            { return id; }
    public String       getTitle()         { return title; }
    public String       getContent()       { return content; }
    public String       getSectionId()     { return sectionId; }
    public boolean      isPinned()         { return isPinned; }
    public long         getCreatedAt()     { return createdAt; }
    public long         getUpdatedAt()     { return updatedAt; }
    public List<String> getTagIds()        { return tagIds; }
    public List<String> getAttachmentIds() { return attachmentIds; }

    // ─────────────────────────────────────────────────────────────────────────
    //  SETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public void setId(String id)                      { this.id = id; }
    public void setTitle(String title)                { this.title = title; }
    public void setContent(String content)            { this.content = content; }
    public void setSectionId(String sectionId)        { this.sectionId = sectionId; }
    public void setPinned(boolean pinned)             { this.isPinned = pinned; }
    public void setCreatedAt(long createdAt)          { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt)          { this.updatedAt = updatedAt; }
    public void setTagIds(List<String> tagIds)        { this.tagIds = tagIds; }
    public void setAttachmentIds(List<String> ids)    { this.attachmentIds = ids; }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /** Updates the updatedAt timestamp to now. */
    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    /** Returns true if the note has no content. */
    public boolean isEmpty() {
        return content == null || content.isBlank();
    }

    @Override
    public String toString() {
        return "Note{id='" + id + "', title='" + title + "', sectionId='" + sectionId + "'}";
    }

}