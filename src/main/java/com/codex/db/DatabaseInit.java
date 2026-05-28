package com.codex.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  DatabaseInit — Utility class for initializing the database schema.
//  Loads SQL commands from schema.sql and executes them to set up tables.
// ─────────────────────────────────────────────────────────────────────────────

public class DatabaseInit {

    // ─────────────────────────────────────────────────────────────────────────
    //  INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Initializes the database schema by loading and executing SQL statements
     * from the schema.sql file.
     */
    public static void initialize() {
        try {
            // Load the schema.sql file as a string
            String schema = loadSchema();
            if (schema == null || schema.trim().isEmpty()) {
                System.err.println("[DatabaseInit] Failed to load schema.sql");
                return;
            }

            // Open a database connection and create a statement
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {

                // Split the schema into individual SQL statements by semicolon
                for (String sql : schema.split(";")) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql); // Execute each SQL statement
                    }
                }

                System.out.println("[DatabaseInit] Database schema initialized successfully");
            }
        } catch (Exception e) {
            // Log any errors that occur during initialization
            System.err.println("[DatabaseInit] Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Loads the schema.sql file from the classpath and returns its contents as a string.
     * - Skips comment lines (starting with "--").
     * - Returns null if the file cannot be found or read.
     */
    private static String loadSchema() {
        try (InputStream input = DatabaseInit.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (input == null) {
                System.err.println("[DatabaseInit] schema.sql not found in classpath");
                return null;
            }

            StringBuilder sb = new StringBuilder();
            // Read the file line by line
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip SQL comments (lines starting with "--")
                    if (!line.trim().startsWith("--")) {
                        sb.append(line).append("\n");
                    }
                }
            }
            return sb.toString(); // Return the full schema as a string
        } catch (Exception e) {
            // Log any errors that occur while loading the schema file
            System.err.println("[DatabaseInit] Failed to load schema.sql: " + e.getMessage());
            return null;
        }
    }
}
