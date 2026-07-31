package com.examseating.dao;

import com.examseating.model.ExamSession;
import com.examseating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ExamSessionDAOImpl - JDBC implementation of ExamSessionDAO.
 */
public class ExamSessionDAOImpl implements ExamSessionDAO {

    @Override
    public void addExam(ExamSession exam) {
        String sql = "INSERT INTO exam_sessions (exam_name, exam_date, exam_time) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, exam.getExamName());
            ps.setDate(2, Date.valueOf(exam.getExamDate()));
            ps.setString(3, exam.getExamTime());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[ExamSessionDAO] Error adding exam: " + e.getMessage());
            throw new RuntimeException("Failed to add exam session", e);
        }
    }

    @Override
    public ExamSession getExamById(int examId) {
        String sql = "SELECT * FROM exam_sessions WHERE exam_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[ExamSessionDAO] Error fetching exam: " + e.getMessage());
            throw new RuntimeException("Failed to fetch exam session", e);
        }
        return null;
    }

    @Override
    public List<ExamSession> getAllExams() {
        String sql = "SELECT * FROM exam_sessions ORDER BY exam_date DESC";
        List<ExamSession> exams = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                exams.add(mapRow(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ExamSessionDAO] Error fetching exams: " + e.getMessage());
            throw new RuntimeException("Failed to fetch exam sessions", e);
        }
        return exams;
    }

    @Override
    public void deleteExam(int examId) {
        String sql = "DELETE FROM exam_sessions WHERE exam_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[ExamSessionDAO] Error deleting exam: " + e.getMessage());
            throw new RuntimeException("Failed to delete exam session", e);
        }
    }

    private ExamSession mapRow(ResultSet rs) throws SQLException {
        ExamSession exam = new ExamSession();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setExamName(rs.getString("exam_name"));
        Date date = rs.getDate("exam_date");
        exam.setExamDate(date != null ? date.toString() : "");
        exam.setExamTime(rs.getString("exam_time"));
        return exam;
    }
}
