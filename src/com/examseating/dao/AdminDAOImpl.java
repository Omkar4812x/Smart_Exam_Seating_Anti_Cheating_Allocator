package com.examseating.dao;

import com.examseating.model.AdminUser;
import com.examseating.util.DBConnection;
import java.sql.*;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public AdminUser authenticate(String username, String hashedPassword) {
        String sql = "SELECT * FROM admin_users WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AdminUser admin = new AdminUser();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Authentication error: " + e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
        return null;
    }
}
