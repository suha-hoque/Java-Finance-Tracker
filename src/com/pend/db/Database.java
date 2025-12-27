package com.pend.db;

import java.sql.*;

public class Database {
    // SQLite file in project root
    private static final String DB_URL = "jdbc:sqlite:finance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table: store plain username for display, plus hashed username & password and salt
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,        -- plain username (used only for display & uniqueness)
                    username_hash TEXT NOT NULL,         -- salted+peppered hash of username (for extra privacy)
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    amount REAL NOT NULL,
                    category TEXT,
                    description TEXT,
                    currency TEXT DEFAULT 'USD',
                    FOREIGN KEY(user_id) REFERENCES users(id)
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
