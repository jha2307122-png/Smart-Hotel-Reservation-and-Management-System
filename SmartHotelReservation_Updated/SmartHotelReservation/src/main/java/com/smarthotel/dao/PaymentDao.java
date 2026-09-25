package com.smarthotel.dao;

import com.smarthotel.database.Database;
import com.smarthotel.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {
    public void create(int rid, double amount, String method, String ref) throws SQLException {
        validate(rid, amount, method);
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "INSERT INTO payments(reservation_id,amount,method,status,transaction_ref) VALUES(?, ?, ?, 'PENDING', ?)")) {
            p.setInt(1, rid);
            p.setDouble(2, amount);
            p.setString(3, method);
            p.setString(4, ref);
            p.executeUpdate();
        }
    }

    public void createPending(int rid, double amount, String method, String ref) throws SQLException {
        validate(rid, amount, method);
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "INSERT INTO payments(reservation_id,amount,method,status,transaction_ref) VALUES(?, ?, ?, 'PENDING', ?)")) {
            p.setInt(1, rid);
            p.setDouble(2, amount);
            p.setString(3, method);
            p.setString(4, ref);
            p.executeUpdate();
        }
    }

    public void markPaid(int id) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE payments SET status='PAID', paid_at=datetime('now','localtime') WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Payment not found.");
        }
    }

    public void markPending(int id) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE payments SET status='PENDING', paid_at=NULL WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Payment not found.");
        }
    }

    public void update(int id, int rid, double amount, String method, String status, String ref) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Invalid payment ID.");
        validate(rid, amount, method);
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Payment status is required.");
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE payments SET reservation_id=?, amount=?, method=?, status=?, transaction_ref=? WHERE id=?")) {
            p.setInt(1, rid);
            p.setDouble(2, amount);
            p.setString(3, method);
            p.setString(4, status);
            p.setString(5, ref);
            p.setInt(6, id);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Payment not found.");
        }
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Invalid payment ID.");
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement("DELETE FROM payments WHERE id=?")) {
            p.setInt(1, id);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Payment not found.");
        }
    }

    public List<Payment> all() throws SQLException {
        List<Payment> list = new ArrayList<>();
        try (Connection c = Database.connect(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM payments ORDER BY id DESC")) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public List<Payment> byStatus(String status) throws SQLException {
        List<Payment> list = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement("SELECT * FROM payments WHERE status=? ORDER BY id DESC")) {
            p.setString(1, status);
            try (ResultSet r = p.executeQuery()) { while (r.next()) list.add(map(r)); }
        }
        return list;
    }

    public List<Payment> search(String query) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String q = "%" + (query == null ? "" : query.trim()) + "%";
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(
                     "SELECT * FROM payments WHERE CAST(reservation_id AS TEXT) LIKE ? OR method LIKE ? OR status LIKE ? ORDER BY id DESC")) {
            p.setString(1, q); p.setString(2, q); p.setString(3, q);
            try (ResultSet r = p.executeQuery()) { while (r.next()) list.add(map(r)); }
        }
        return list;
    }

    private Payment map(ResultSet r) throws SQLException {
        return new Payment(r.getInt("id"), r.getInt("reservation_id"), r.getDouble("amount"),
                r.getString("method"), r.getString("status"), r.getString("transaction_ref"), r.getString("paid_at"));
    }

    public List<Payment> byReservation(int rid) throws SQLException {
        List<Payment> x = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement("SELECT * FROM payments WHERE reservation_id=? ORDER BY id DESC")) {
            p.setInt(1, rid);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) x.add(map(r));
            }
        }
        return x;
    }

    public String statusForReservation(int rid) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement("SELECT status FROM payments WHERE reservation_id=? ORDER BY id DESC LIMIT 1")) {
            p.setInt(1, rid);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getString(1) : "UNPAID";
            }
        }
    }

    private void validate(int rid, double amount, String method) {
        if (rid <= 0) throw new IllegalArgumentException("Invalid reservation ID.");
        if (amount <= 0) throw new IllegalArgumentException("Payment amount must be greater than zero.");
        if (method == null || method.isBlank()) throw new IllegalArgumentException("Payment method is required.");
    }
}
