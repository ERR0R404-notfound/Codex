package com.codex.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.codex.model.Note;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  NoteDAO — Data Access Object for notes.
//  Provides CRUD operations and supports relationships with tags and attachments.
// ─────────────────────────────────────────────────────────────────────────────

public class NoteDAO {

    // ─────────────────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Inserts a new note into the database.
     * - Sets title, content, section ID, pinned status, and timestamps.
     * - Retrieves the auto-generated ID and assigns it back to the Note object.
     */
    public Note create(Note note) throws Exception {
        String sql = "INSERT INTO notes (title, content, section_id, is_pinned, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setInt(3, Integer.parseInt(note.getSectionId()));
            pstmt.setBoolean(4, note.isPinned());
            pstmt.setLong(5, System.currentTimeMillis()); // created_at timestamp
            pstmt.setLong(6, System.currentTimeMillis()); // updated_at timestamp

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        note.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }
        }
        return note;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Reads a single note by its ID.
     * - Also loads associated tags and attachments for completeness.
     */
    public Note read(int id) throws Exception {
        String sql = "SELECT id, title, content, section_id, is_pinned, created_at, updated_at FROM notes WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    loadTagsForNote(conn, note);
                    loadAttachmentsForNote(conn, note);
                    return note;
                }
            }
        }
        return null;
    }

    /** Reads all notes in a given section, ordered by pinned status and creation time. */
    public List<Note> readBySection(int sectionId) throws Exception {
        String sql = "SELECT id, title, content, section_id, is_pinned, created_at, updated_at FROM notes WHERE section_id = ? ORDER BY is_pinned DESC, created_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    loadTagsForNote(conn, note);
                    loadAttachmentsForNote(conn, note);
                    notes.add(note);
                }
            }
        }
        return notes;
    }

    /** Reads all pinned notes in a given section. */
    public List<Note> readPinned(int sectionId) throws Exception {
        String sql = "SELECT id, title, content, section_id, is_pinned, created_at, updated_at FROM notes WHERE section_id = ? AND is_pinned = TRUE ORDER BY created_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    loadTagsForNote(conn, note);
                    loadAttachmentsForNote(conn, note);
                    notes.add(note);
                }
            }
        }
        return notes;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────────
    /** Updates an existing note record with new values. */
    public void update(Note note) throws Exception {
        String sql = "UPDATE notes SET title = ?, content = ?, section_id = ?, is_pinned = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setInt(3, Integer.parseInt(note.getSectionId()));
            pstmt.setBoolean(4, note.isPinned());
            pstmt.setLong(5, System.currentTimeMillis()); // update timestamp
            pstmt.setInt(6, Integer.parseInt(note.getId()));

            pstmt.executeUpdate();
        }
    }

    /** Updates only the content of a note (useful for quick edits). */
    public void updateContent(int id, String content) throws Exception {
        String sql = "UPDATE notes SET content = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setInt(3, id);

            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────────────────────────────────
    /** Deletes a note by its ID. */
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SEARCH
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Searches notes by title or content using a LIKE query.
     * - Returns up to 100 results ordered by creation time.
     */
    public List<Note> search(String query) throws Exception {
        String sql = "SELECT id, title, content, section_id, is_pinned, created_at, updated_at FROM notes WHERE title LIKE ? OR content LIKE ? ORDER BY created_at DESC LIMIT 100";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + query + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = mapResultSetToNote(rs);
                    loadTagsForNote(conn, note);
                    loadAttachmentsForNote(conn, note);
                    notes.add(note);
                }
            }
        }
        return notes;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────
    /** Converts a ResultSet row into a Note object. */
    private Note mapResultSetToNote(ResultSet rs) throws Exception {
        Note note = new Note();
        note.setId(String.valueOf(rs.getInt("id")));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setSectionId(String.valueOf(rs.getInt("section_id")));
        note.setPinned(rs.getBoolean("is_pinned"));
        note.setCreatedAt(rs.getLong("created_at"));
        note.setUpdatedAt(rs.getLong("updated_at"));
        return note;
    }

    /** Loads tag IDs associated with a note. */
    private void loadTagsForNote(Connection conn, Note note) throws Exception {
        String sql = "SELECT tag_id FROM note_tags WHERE note_id = ?";
        List<String> tagIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(note.getId()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tagIds.add(String.valueOf(rs.getInt("tag_id")));
                }
            }
        }
        note.setTagIds(tagIds);
    }

    /** Loads attachment IDs associated with a note. */
    private void loadAttachmentsForNote(Connection conn, Note note) throws Exception {
        String sql = "SELECT id FROM attachments WHERE note_id = ?";
        List<String> attachmentIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(note.getId()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attachmentIds.add(String.valueOf(rs.getInt("id")));
                }
            }
        }
    }
}
