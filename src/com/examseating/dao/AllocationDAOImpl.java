package com.examseating.dao;

import com.examseating.model.SeatAllocation;
import com.examseating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AllocationDAOImpl - JDBC implementation of AllocationDAO.
 * Handles batch insert of allocations and JOIN queries for display.
 */
public class AllocationDAOImpl implements AllocationDAO {

    @Override
    public void saveAllocations(List<SeatAllocation> allocations) {
        String sql = "INSERT INTO seat_allocation (exam_id, student_id, room_id, seat_row, seat_col) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (SeatAllocation alloc : allocations) {
                ps.setInt(1, alloc.getExamId());
                ps.setInt(2, alloc.getStudentId());
                ps.setInt(3, alloc.getRoomId());
                ps.setInt(4, alloc.getSeatRow());
                ps.setInt(5, alloc.getSeatCol());
                ps.addBatch();
            }
            
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            
            System.out.println("[AllocationDAO] Saved " + allocations.size() + " allocations.");
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error saving allocations: " + e.getMessage());
            throw new RuntimeException("Failed to save seat allocations", e);
        }
    }

    @Override
    public List<SeatAllocation> getAllocationsByExam(int examId) {
        String sql = "SELECT sa.*, s.roll_no, s.name AS student_name, s.subject_code, " +
                     "s.class_year, s.branch, r.room_no " +
                     "FROM seat_allocation sa " +
                     "JOIN students s ON sa.student_id = s.student_id " +
                     "JOIN rooms r ON sa.room_id = r.room_id " +
                     "WHERE sa.exam_id = ? " +
                     "ORDER BY r.room_no, sa.seat_row, sa.seat_col";
        
        List<SeatAllocation> allocations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRow(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error fetching allocations: " + e.getMessage());
            throw new RuntimeException("Failed to fetch allocations", e);
        }
        return allocations;
    }

    @Override
    public List<SeatAllocation> getAllocationsByRoom(int examId, int roomId) {
        String sql = "SELECT sa.*, s.roll_no, s.name AS student_name, s.subject_code, " +
                     "s.class_year, s.branch, r.room_no " +
                     "FROM seat_allocation sa " +
                     "JOIN students s ON sa.student_id = s.student_id " +
                     "JOIN rooms r ON sa.room_id = r.room_id " +
                     "WHERE sa.exam_id = ? AND sa.room_id = ? " +
                     "ORDER BY sa.seat_row, sa.seat_col";
        
        List<SeatAllocation> allocations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRow(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error fetching room allocations: " + e.getMessage());
            throw new RuntimeException("Failed to fetch room allocations", e);
        }
        return allocations;
    }

    @Override
    public void deleteAllocationsByExam(int examId) {
        String sql = "DELETE FROM seat_allocation WHERE exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            int deleted = ps.executeUpdate();
            System.out.println("[AllocationDAO] Deleted " + deleted + " allocations for exam " + examId);
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error deleting allocations: " + e.getMessage());
            throw new RuntimeException("Failed to delete allocations", e);
        }
    }

    @Override
    public SeatAllocation findByRollNoAndExam(String rollNo, int examId) {
        String sql = "SELECT sa.*, s.roll_no, s.name AS student_name, s.subject_code, " +
                     "s.class_year, s.branch, r.room_no " +
                     "FROM seat_allocation sa " +
                     "JOIN students s ON sa.student_id = s.student_id " +
                     "JOIN rooms r ON sa.room_id = r.room_id " +
                     "WHERE s.roll_no = ? AND sa.exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rollNo);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error finding allocation: " + e.getMessage());
            throw new RuntimeException("Failed to find allocation", e);
        }
        return null;
    }

    @Override
    public boolean hasAllocations(int examId) {
        String sql = "SELECT COUNT(*) FROM seat_allocation WHERE exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("[AllocationDAO] Error checking allocations: " + e.getMessage());
        }
        return false;
    }

    private SeatAllocation mapRow(ResultSet rs) throws SQLException {
        SeatAllocation alloc = new SeatAllocation();
        alloc.setAllocationId(rs.getInt("allocation_id"));
        alloc.setExamId(rs.getInt("exam_id"));
        alloc.setStudentId(rs.getInt("student_id"));
        alloc.setRoomId(rs.getInt("room_id"));
        alloc.setSeatRow(rs.getInt("seat_row"));
        alloc.setSeatCol(rs.getInt("seat_col"));
        alloc.setRollNo(rs.getString("roll_no"));
        alloc.setStudentName(rs.getString("student_name"));
        alloc.setSubjectCode(rs.getString("subject_code"));
        alloc.setClassYear(rs.getString("class_year"));
        alloc.setBranch(rs.getString("branch"));
        alloc.setRoomNo(rs.getString("room_no"));
        return alloc;
    }
}
