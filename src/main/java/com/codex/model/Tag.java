package com.codex.model;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  Tag — represents a label that can be applied to notes.
//  Tags are global (not section-specific) and reusable across notes.
// ─────────────────────────────────────────────────────────────────────────────

public class Tag {

    // ─────────────────────────────────────────────────────────────────────────
    //  ATTRIBUTES / FIELDS
    // ─────────────────────────────────────────────────────────────────────────

    private String id;          // Firebase-generated key
    private String name;        // Display name e.g. "ITS132P"
    private String color;       // Hex color code e.g. "#FF5733"
    private long   createdAt;   // Unix timestamp

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTORS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Default constructor — required by Firebase for deserialization.
     */
    public Tag() {}

    /**
     * Convenience constructor for creating a new tag.
     */
    public Tag(String name, String color) {
        this.name      = name;
        this.color     = color;
        this.createdAt = System.currentTimeMillis();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public String getId()        { return id; }
    public String getName()      { return name; }
    public String getColor()     { return color; }
    public long   getCreatedAt() { return createdAt; }

    // ─────────────────────────────────────────────────────────────────────────
    //  SETTERS
    // ─────────────────────────────────────────────────────────────────────────

    public void setId(String id)             { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setColor(String color)       { this.color = color; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Tag{id='" + id + "', name='" + name + "', color='" + color + "'}";
    }

}