package com.examseating.dao;

import com.examseating.model.Student;
import com.examseating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAOImpl - JDBC implementation of StudentDAO.
 * Supports single insert and batch insert (for CSV upload).
 */
public class StudentDAOImpl implements StudentDAO {

    @Override
    public void addStudent(Student student) {
        String sql = "INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getName());
            ps.setString(3, student.getBranch());
            ps.setString(4, student.getClassYear());
            ps.setString(5, student.getSubjectCode());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error adding student: " + e.getMessage());
            throw new RuntimeException("Failed to add student", e);
        }
    }

    @Override
    public void addStudentsBatch(List<Student> students) {
        String sql = "INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Disable auto-commit for batch performance
            conn.setAutoCommit(false);
            
            int batchSize = 0;
            for (Student s : students) {
                ps.setString(1, s.getRollNo());
                ps.setString(2, s.getName());
                ps.setString(3, s.getBranch());
                ps.setString(4, s.getClassYear());
                ps.setString(5, s.getSubjectCode());
                ps.addBatch();
                
                batchSize++;
                // Execute every 500 records to avoid memory issues
                if (batchSize % 500 == 0) {
                    ps.executeBatch();
                }
            }
            
            // Execute remaining batch
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            
            System.out.println("[StudentDAO] Batch inserted " + students.size() + " students.");
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error in batch insert: " + e.getMessage());
            throw new RuntimeException("Failed to batch insert students", e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM students ORDER BY roll_no";
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                students.add(mapRow(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error fetching students: " + e.getMessage());
            throw new RuntimeException("Failed to fetch students", e);
        }
        return students;
    }

    @Override
    public Student getStudentByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error fetching student: " + e.getMessage());
            throw new RuntimeException("Failed to fetch student", e);
        }
        return null;
    }

    @Override
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error fetching student: " + e.getMessage());
            throw new RuntimeException("Failed to fetch student", e);
        }
        return null;
    }

    @Override
    public void deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error deleting student: " + e.getMessage());
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    @Override
    public int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) return rs.getInt(1);
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error counting students: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void deleteAllStudents() {
        String sql = "DELETE FROM students";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error deleting all students: " + e.getMessage());
            throw new RuntimeException("Failed to delete all students", e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setName(rs.getString("name"));
        s.setBranch(rs.getString("branch"));
        s.setClassYear(rs.getString("class_year"));
        s.setSubjectCode(rs.getString("subject_code"));
        return s;
    }
}
