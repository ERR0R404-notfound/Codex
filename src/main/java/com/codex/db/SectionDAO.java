package com.codex.db;

import com.codex.model.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public Section create(Section section) throws Exception {
        String sql = "INSERT INTO sections (name, parent_id, `order`, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, section.getName());
            pstmt.setObject(2, section.getParentId() != null ? Integer.parseInt(section.getParentId()) : null);
            pstmt.setInt(3, 0);
            pstmt.setLong(4, System.currentTimeMillis());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String id = String.valueOf(generatedKeys.getInt(1));
                        section.setId(id);
                    }
                }
            }
        }
        return section;
    }

    public Section read(int id) throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSection(rs);
                }
            }
        }
        return null;
    }

    public List<Section> readByParent(int parentId) throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE parent_id = ? ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }
        }
        return sections;
    }

    public List<Section> readRoot() throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE parent_id IS NULL ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }
        }
        return sections;
    }

    public List<Section> readAll() throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }
        }
        return sections;
    }

    public void update(Section section) throws Exception {
        String sql = "UPDATE sections SET name = ?, parent_id = ?, `order` = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, section.getName());
            pstmt.setObject(2, section.getParentId() != null ? Integer.parseInt(section.getParentId()) : null);
            pstmt.setInt(3, 0);
            pstmt.setInt(4, Integer.parseInt(section.getId()));

            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM sections WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Section mapResultSetToSection(ResultSet rs) throws Exception {
        Section section = new Section();
        section.setId(String.valueOf(rs.getInt("id")));
        section.setName(rs.getString("name"));
        int parentId = rs.getInt("parent_id");
        section.setParentId(rs.wasNull() ? null : String.valueOf(parentId));
        section.setCreatedAt(rs.getLong("created_at"));
        return section;
    }
}
