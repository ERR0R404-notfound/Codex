package com.codex.db;

import com.codex.model.Attachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

    public Attachment create(Attachment attachment) throws Exception {
        String sql = "INSERT INTO attachments (note_id, file_name, file_path, file_type, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, Integer.parseInt(attachment.getNoteId()));
            pstmt.setString(2, attachment.getFileName());
            pstmt.setString(3, attachment.getFilePath());
            pstmt.setString(4, attachment.getFileType());
            pstmt.setLong(5, System.currentTimeMillis());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String id = String.valueOf(generatedKeys.getInt(1));
                        attachment.setId(id);
                    }
                }
            }
        }
        return attachment;
    }

    public Attachment read(int id) throws Exception {
        String sql = "SELECT id, note_id, file_name, file_path, file_type, created_at FROM attachments WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttachment(rs);
                }
            }
        }
        return null;
    }

    public List<Attachment> readByNote(int noteId) throws Exception {
        String sql = "SELECT id, note_id, file_name, file_path, file_type, created_at FROM attachments WHERE note_id = ? ORDER BY created_at DESC";
        List<Attachment> attachments = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToAttachment(rs));
                }
            }
        }
        return attachments;
    }

    public void update(Attachment attachment) throws Exception {
        String sql = "UPDATE attachments SET file_name = ?, file_path = ?, file_type = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, attachment.getFileName());
            pstmt.setString(2, attachment.getFilePath());
            pstmt.setString(3, attachment.getFileType());
            pstmt.setInt(4, Integer.parseInt(attachment.getId()));

            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM attachments WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteByNote(int noteId) throws Exception {
        String sql = "DELETE FROM attachments WHERE note_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        }
    }

    private Attachment mapResultSetToAttachment(ResultSet rs) throws Exception {
        Attachment attachment = new Attachment();
        attachment.setId(String.valueOf(rs.getInt("id")));
        attachment.setNoteId(String.valueOf(rs.getInt("note_id")));
        attachment.setFileName(rs.getString("file_name"));
        attachment.setFilePath(rs.getString("file_path"));
        attachment.setFileType(rs.getString("file_type"));
        attachment.setCreatedAt(rs.getLong("created_at"));
        return attachment;
    }
}
