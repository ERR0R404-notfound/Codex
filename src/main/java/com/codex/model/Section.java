package com.codex.model;

// ─────────────────────────────────────────────────────────────────────────────
//  IMPORTS
// ─────────────────────────────────────────────────────────────────────────────

import java.util.ArrayList;
import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Section — represents a notebook section (folder) in Codex.
//  Sections can be nested via parentId (null = root level).
//  A section can contain both notes and child sections.
// ─────────────────────────────────────────────────────────────────────────────

public class Section {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private String       id;           // Firebase-generated key
    private String       name;         // Display name
    private String       parentId;     // null = root section
    private int          order;        // Display order among siblings
    private long         createdAt;    // Unix timestamp
    private List<String> noteIds;      // IDs of notes in this section
    private List<String> childIds;     // IDs of child sections

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTORS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Default constructor — required by Firebase for deserialization.
     */
    public Section() {
        this.noteIds  = new ArrayList<>();
        this.childIds = new ArrayList<>();
    }

    /**
     * Convenience constructor for creating a new root section.
     */
    public Section(String name) {
        this();
        this.name      = name;
        this.parentId  = null;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Convenience constructor for creating a nested section.
     */
    public Section(String name, String parentId) {
        this();
        this.name      = name;
        this.parentId  = parentId;
        this.createdAt = System.currentTimeMillis();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public String       getId()        { return id; }
    public String       getName()      { return name; }
    public String       getParentId()  { return parentId; }
    public int          getOrder()     { return order; }
    public long         getCreatedAt() { return createdAt; }
    public List<String> getNoteIds()   { return noteIds; }
    public List<String> getChildIds()  { return childIds; }

    // ─────────────────────────────────────────────────────────────────────────
    //  SETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public void setId(String id)              { this.id = id; }
    public void setName(String name)          { this.name = name; }
    public void setParentId(String parentId)  { this.parentId = parentId; }
    public void setOrder(int order)           { this.order = order; }
    public void setCreatedAt(long createdAt)  { this.createdAt = createdAt; }
    public void setNoteIds(List<String> ids)  { this.noteIds = ids; }
    public void setChildIds(List<String> ids) { this.childIds = ids; }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns true if this is a root-level section. */
    public boolean isRoot() {
        return parentId == null || parentId.isEmpty();
    }

    @Override
    public String toString() {
        return "Section{id='" + id + "', name='" + name + "', parentId='" + parentId + "'}";
    }

}