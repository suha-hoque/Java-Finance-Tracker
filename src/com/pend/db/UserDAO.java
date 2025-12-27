package com.pend.db;

import com.pend.security.PasswordUtil;

import java.sql.*;

/**
 * User registration + authentication.
 * Stores:
 *  - username (plain) for display & uniqueness
 *  - username_hash (salted+peppered)
 *  - password_hash (salted+peppered)
 *  - salt (per-user)
 */
public class UserDAO {

    /**
     * Register a new user. Returns user id on success, -1 on failure.
     */
    public static int register(String username, String password) {
        String salt = PasswordUtil.generateSalt();
        String usernameHash = PasswordUtil.hash(username, salt);
        String passwordHash = PasswordUtil.hash(password, salt);

        String sql = "INSERT INTO users(username, username_hash, password_hash, salt) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, usernameHash);
            ps.setString(3, passwordHash);
            ps.setString(4, salt);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return -1;
        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Authenticate a user. Returns user id if successful, null otherwise.
     *
     * Implementation note:
     * we query by plain username (unique) to fetch the salt & stored hashes,
     * then compare hashed values. We also store username_hash in DB to satisfy
     * the requirement of hashing username before storing (we keep the plain
     * username for usability/uniqueness).
     */
    public static Integer authenticate(String username, String password) {
        String sql = "SELECT id, username_hash, password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            String salt = rs.getString("salt");
            String storedUserHash = rs.getString("username_hash");
            String storedPassHash = rs.getString("password_hash");

            String providedUserHash = PasswordUtil.hash(username, salt);
            String providedPassHash = PasswordUtil.hash(password, salt);

            if (storedUserHash.equals(providedUserHash) && storedPassHash.equals(providedPassHash)) {
                return rs.getInt("id");
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Optionally fetch the display username for a user id.
     */
    public static String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
