package com.codex.db;

import com.codex.model.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public Tag create(Tag tag) throws Exception {
        String sql = "INSERT INTO tags (name, color, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tag.getName());
            pstmt.setString(2, tag.getColor());
            pstmt.setLong(3, System.currentTimeMillis());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String id = String.valueOf(generatedKeys.getInt(1));
                        tag.setId(id);
                    }
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                return readByName(tag.getName());
            }
            throw e;
        }
        return tag;
    }

    public Tag read(int id) throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
        }
        return null;
    }

    public Tag readByName(String name) throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags WHERE name = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTag(rs);
                }
            }
        }
        return null;
    }

    public List<Tag> readAll() throws Exception {
        String sql = "SELECT id, name, color, created_at FROM tags ORDER BY name";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapResultSetToTag(rs));
                }
            }
        }
        return tags;
    }

    public List<Tag> readByNote(int noteId) throws Exception {
        String sql = "SELECT t.id, t.name, t.color, t.created_at FROM tags t " +
                     "JOIN note_tags nt ON t.id = nt.tag_id WHERE nt.note_id = ? ORDER BY t.name";
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapResultSetToTag(rs));
                }
            }
        }
        return tags;
    }

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

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void addTagToNote(int noteId, int tagId) throws Exception {
        String sql = "INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE note_id=note_id";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.setInt(2, tagId);

            pstmt.executeUpdate();
        }
    }

    public void removeTagFromNote(int noteId, int tagId) throws Exception {
        String sql = "DELETE FROM note_tags WHERE note_id = ? AND tag_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.setInt(2, tagId);

            pstmt.executeUpdate();
        }
    }

    private Tag mapResultSetToTag(ResultSet rs) throws Exception {
        Tag tag = new Tag();
        tag.setId(String.valueOf(rs.getInt("id")));
        tag.setName(rs.getString("name"));
        tag.setColor(rs.getString("color"));
        tag.setCreatedAt(rs.getLong("created_at"));
        return tag;
    }
}
