package com.codex.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.codex.util.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// ─────────────────────────────────────────────────────────────────────────────
//  CLASS DECLARATION
//  DatabaseManager — Singleton class that manages database connections.
//  Uses HikariCP connection pooling for efficient and reliable access.
// ─────────────────────────────────────────────────────────────────────────────

public class DatabaseManager {
    // Singleton instance of DatabaseManager
    private static DatabaseManager instance;
    // HikariCP connection pool
    private HikariDataSource dataSource;

    // ─────────────────────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the connection pool when the singleton is created.
     */
    private DatabaseManager() {
        initializeDataSource();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SINGLETON ACCESSOR
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Returns the singleton instance of DatabaseManager.
     * Uses double-checked locking for thread safety.
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  INITIALIZATION
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Initializes the HikariCP connection pool using configuration values.
     * Reads database properties from AppConfig and applies them to HikariConfig.
     */
    private void initializeDataSource() {
        try {
            AppConfig config = AppConfig.getInstance();

            // Configure HikariCP with database settings
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getProperty("db.url"));
            hikariConfig.setUsername(config.getProperty("db.username"));
            hikariConfig.setPassword(config.getProperty("db.password", ""));
            hikariConfig.setMinimumIdle(config.getIntProperty("db.pool.minimumIdle", 5));
            hikariConfig.setMaximumPoolSize(config.getIntProperty("db.pool.maximumPoolSize", 20));
            hikariConfig.setIdleTimeout(config.getLongProperty("db.pool.idleTimeout", 600000)); // 10 minutes
            hikariConfig.setMaxLifetime(config.getLongProperty("db.pool.maxLifetime", 1800000)); // 30 minutes
            hikariConfig.setConnectionTestQuery("SELECT 1"); // simple query to validate connections

            // Create the data source (connection pool)
            dataSource = new HikariDataSource(hikariConfig);
            System.out.println("[DatabaseManager] Connection pool initialized successfully");
        } catch (Exception e) {
            // Log and rethrow if initialization fails
            System.err.println("[DatabaseManager] Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CONNECTION ACCESS
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Provides a connection from the pool.
     * @return A valid SQL Connection object.
     * @throws SQLException if the pool is not initialized or connection fails.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return dataSource.getConnection();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SHUTDOWN
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Shuts down the connection pool gracefully.
     * Closes all pooled connections to release resources.
     */
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("[DatabaseManager] Connection pool closed");
        }
    }
}
