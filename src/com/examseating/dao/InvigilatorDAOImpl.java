package com.examseating.dao;

import com.examseating.model.Invigilator;
import com.examseating.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvigilatorDAOImpl implements InvigilatorDAO {

    @Override
    public void addInvigilator(Invigilator inv) {
        String sql = "INSERT INTO invigilators (name, department) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inv.getName());
            ps.setString(2, inv.getDepartment());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add invigilator", e);
        }
    }

    @Override
    public void deleteInvigilator(int invigilatorId) {
        String sql = "DELETE FROM invigilators WHERE invigilator_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invigilatorId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete invigilator", e);
        }
    }

    @Override
    public Invigilator getInvigilatorById(int invigilatorId) {
        String sql = "SELECT * FROM invigilators WHERE invigilator_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invigilatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch invigilator", e);
        }
        return null;
    }

    @Override
    public List<Invigilator> getAllInvigilators() {
        String sql = "SELECT * FROM invigilators ORDER BY name";
        List<Invigilator> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch invigilators", e);
        }
        return list;
    }

    @Override
    public int getInvigilatorCount() {
        String sql = "SELECT COUNT(*) FROM invigilators";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { /* ignore */ }
        return 0;
    }

    private Invigilator mapRow(ResultSet rs) throws SQLException {
        Invigilator inv = new Invigilator();
        inv.setInvigilatorId(rs.getInt("invigilator_id"));
        inv.setName(rs.getString("name"));
        inv.setDepartment(rs.getString("department"));
        return inv;
    }
}
