package com.codex.db;

import java.sql.Connection;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseInit {

    public static void initialize() {
        try {
            String schema = loadSchema();
            if (schema == null || schema.trim().isEmpty()) {
                System.err.println("[DatabaseInit] Failed to load schema.sql");
                return;
            }

            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {

                for (String sql : schema.split(";")) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                }

                System.out.println("[DatabaseInit] Database schema initialized successfully");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInit] Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String loadSchema() {
        try (InputStream input = DatabaseInit.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (input == null) {
                System.err.println("[DatabaseInit] schema.sql not found in classpath");
                return null;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("--")) {
                        sb.append(line).append("\n");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("[DatabaseInit] Failed to load schema.sql: " + e.getMessage());
            return null;
        }
    }
}
