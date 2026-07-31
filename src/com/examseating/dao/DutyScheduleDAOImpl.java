package com.examseating.dao;

import com.examseating.model.DutySchedule;
import com.examseating.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DutyScheduleDAOImpl implements DutyScheduleDAO {

    @Override
    public void saveDuties(List<DutySchedule> duties) {
        String sql = "INSERT INTO duty_schedule (exam_id, room_id, invigilator_id, duty_slot) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (DutySchedule d : duties) {
                ps.setInt(1, d.getExamId());
                ps.setInt(2, d.getRoomId());
                ps.setInt(3, d.getInvigilatorId());
                ps.setString(4, d.getDutySlot());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save duty schedule", e);
        }
    }

    @Override
    public List<DutySchedule> getDutiesByExam(int examId) {
        String sql = "SELECT ds.*, i.name AS invigilator_name, i.department, r.room_no, es.exam_name " +
                     "FROM duty_schedule ds " +
                     "JOIN invigilators i ON ds.invigilator_id = i.invigilator_id " +
                     "JOIN rooms r ON ds.room_id = r.room_id " +
                     "JOIN exam_sessions es ON ds.exam_id = es.exam_id " +
                     "WHERE ds.exam_id = ? ORDER BY r.room_no";
        List<DutySchedule> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DutySchedule d = new DutySchedule();
                    d.setDutyId(rs.getInt("duty_id"));
                    d.setExamId(rs.getInt("exam_id"));
                    d.setRoomId(rs.getInt("room_id"));
                    d.setInvigilatorId(rs.getInt("invigilator_id"));
                    d.setDutySlot(rs.getString("duty_slot"));
                    d.setInvigilatorName(rs.getString("invigilator_name"));
                    d.setDepartment(rs.getString("department"));
                    d.setRoomNo(rs.getString("room_no"));
                    d.setExamName(rs.getString("exam_name"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch duty schedule", e);
        }
        return list;
    }

    @Override
    public void deleteDutiesByExam(int examId) {
        String sql = "DELETE FROM duty_schedule WHERE exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete duty schedule", e);
        }
    }

    @Override
    public boolean hasDuties(int examId) {
        String sql = "SELECT COUNT(*) FROM duty_schedule WHERE exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { /* ignore */ }
        return false;
    }
}
