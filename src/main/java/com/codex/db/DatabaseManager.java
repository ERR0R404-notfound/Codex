package com.codex.db;

import com.codex.util.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;

    private DatabaseManager() {
        initializeDataSource();
    }

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

    private void initializeDataSource() {
        try {
            AppConfig config = AppConfig.getInstance();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getProperty("db.url"));
            hikariConfig.setUsername(config.getProperty("db.username"));
            hikariConfig.setPassword(config.getProperty("db.password", ""));
            hikariConfig.setMinimumIdle(config.getIntProperty("db.pool.minimumIdle", 5));
            hikariConfig.setMaximumPoolSize(config.getIntProperty("db.pool.maximumPoolSize", 20));
            hikariConfig.setIdleTimeout(config.getLongProperty("db.pool.idleTimeout", 600000));
            hikariConfig.setMaxLifetime(config.getLongProperty("db.pool.maxLifetime", 1800000));
            hikariConfig.setConnectionTestQuery("SELECT 1");

            dataSource = new HikariDataSource(hikariConfig);
            System.out.println("[DatabaseManager] Connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("[DatabaseManager] Connection pool closed");
        }
    }
}
