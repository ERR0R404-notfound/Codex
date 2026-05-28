package com.codex.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.codex.model.Section;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  SectionDAO — Data Access Object for sections.
//  Provides CRUD operations and supports hierarchical organization
//  (sections can have parent/child relationships).
// ─────────────────────────────────────────────────────────────────────────────

public class SectionDAO {

    // ─────────────────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Inserts a new section into the database.
     * - Sets the section name, optional parent ID, default order, and timestamp.
     * - Retrieves the auto-generated ID and assigns it back to the Section object.
     */
    public Section create(Section section) throws Exception {
        String sql = "INSERT INTO sections (name, parent_id, `order`, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, section.getName());
            pstmt.setObject(2, section.getParentId() != null ? Integer.parseInt(section.getParentId()) : null);
            pstmt.setInt(3, 0); // default order value
            pstmt.setLong(4, System.currentTimeMillis()); // created_at timestamp

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        section.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }
        }
        return section;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────────────
    /** Reads a single section by its ID. */
    public Section read(int id) throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToSection(rs);
            }
        }
        return null;
    }

    /** Reads all child sections under a given parent section. */
    public List<Section> readByParent(int parentId) throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE parent_id = ? ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) sections.add(mapResultSetToSection(rs));
            }
        }
        return sections;
    }

    /** Reads all root sections (those without a parent). */
    public List<Section> readRoot() throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections WHERE parent_id IS NULL ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) sections.add(mapResultSetToSection(rs));
        }
        return sections;
    }

    /** Reads all sections in the database. */
    public List<Section> readAll() throws Exception {
        String sql = "SELECT id, name, parent_id, `order`, created_at FROM sections ORDER BY `order`, name";
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) sections.add(mapResultSetToSection(rs));
        }
        return sections;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Updates an existing section record.
     * - Allows changing the name, parent ID, and order.
     */
    public void update(Section section) throws Exception {
        String sql = "UPDATE sections SET name = ?, parent_id = ?, `order` = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, section.getName());
            pstmt.setObject(2, section.getParentId() != null ? Integer.parseInt(section.getParentId()) : null);
            pstmt.setInt(3, 0); // default order value
            pstmt.setInt(4, Integer.parseInt(section.getId()));

            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────────────────────────────────
    /** Deletes a section by its ID. */
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM sections WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Converts a ResultSet row into a Section object.
     * - Handles nullable parent_id properly using rs.wasNull().
     */
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
