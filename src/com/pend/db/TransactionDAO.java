package com.pend.db;

import com.pend.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public static void addTransaction(int userId, Transaction t) {
        String sql = "INSERT INTO transactions(user_id, date, amount, category, description, currency) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, t.getDate().toString());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getCategory());
            ps.setString(5, t.getDescription());
            ps.setString(6, t.getCurrency());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTransaction(Transaction t) {
        String sql = "UPDATE transactions SET date=?, amount=?, category=?, description=?, currency=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getDate().toString());
            ps.setDouble(2, t.getAmount());
            ps.setString(3, t.getCategory());
            ps.setString(4, t.getDescription());
            ps.setString(5, t.getCurrency());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Transaction> getTransactionsForUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id=? ORDER BY date DESC, id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                    rs.getInt("id"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("currency")
                );
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
