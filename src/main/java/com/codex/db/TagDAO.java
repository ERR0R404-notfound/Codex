package com.codex.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.codex.model.Tag;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  TagDAO — Data Access Object for tags.
//  Provides CRUD operations and manages associations between notes and tags.
// ─────────────────────────────────────────────────────────────────────────────

public class TagDAO {

    // ─────────────────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Inserts a new tag into the database.
     * If a tag with the same name already exists, it avoids duplication
     * by returning the existing tag instead.
     */
    public Tag create(Tag tag) throws Exception {
        String sql = "INSERT INTO tags (name, color, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tag.getName());
            pstmt.setString(2, tag.getColor());
            pstmt.setLong(3, System.currentTimeMillis());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Retrieve auto-generated ID from DB
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tag.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            // Special handling: if duplicate entry error occurs, fetch existing tag
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                return readByName(tag.getName());
            }
            throw e;
        }
        return tag;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────────────
    /** Reads a tag by its ID. */
    public Tag read(int id) throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToTag(rs);
            }
        }
        return null;
    }

    /** Reads a tag by its name (useful for duplicate checks). */
    public Tag readByName(String name) throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags WHERE name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToTag(rs);
            }
        }
        return null;
    }

    /** Reads all tags in the database, ordered alphabetically. */
    public List<Tag> readAll() throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags ORDER BY name";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) tags.add(mapResultSetToTag(rs));
        }
        return tags;
    }

    /** Reads all tags linked to a specific note. */
    public List<Tag> readByNote(int noteId) throws Exception {
        String sql = "SELECT t.id, t.name, t.color, t.created_at FROM tags t " +
                     "JOIN note_tags nt ON t.id = nt.tag_id WHERE nt.note_id = ? ORDER BY t.name";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) tags.add(mapResultSetToTag(rs));
            }
        }
        return tags;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────────
    /** Updates an existing tag’s name and color. */
    public void update(Tag tag) throws Exception {
        String sql = "UPDATE tags SET name = ?, color = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tag.getName());
            pstmt.setString(2, tag.getColor());
            pstmt.setInt(3, Integer.parseInt(tag.getId()));
            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────────────────────────────────
    /** Deletes a tag by its ID. */
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  NOTE-TAG ASSOCIATIONS
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Associates a tag with a note.
     * Uses ON DUPLICATE KEY UPDATE to prevent duplicate relationships.
     */
    public void addTagToNote(int noteId, int tagId) throws Exception {
        String sql = "INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE note_id=note_id";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.setInt(2, tagId);
            pstmt.executeUpdate();
        }
    }

    /** Removes a tag association from a note. */
    public void removeTagFromNote(int noteId, int tagId) throws Exception {
        String sql = "DELETE FROM note_tags WHERE note_id = ? AND tag_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.setInt(2, tagId);
            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────
    /** Converts a ResultSet row into a Tag object. */
    private Tag mapResultSetToTag(ResultSet rs) throws Exception {
        Tag tag = new Tag();
        tag.setId(String.valueOf(rs.getInt("id")));
        tag.setName(rs.getString("name"));
        tag.setColor(rs.getString("color"));
        tag.setCreatedAt(rs.getLong("created_at"));
        return tag;
    }
}
