package com.examseating.dao;

import com.examseating.model.Student;
import java.util.List;

/**
 * StudentDAO - Data Access Object interface for Student entity.
 */
public interface StudentDAO {
    
    /** Insert a single student */
    void addStudent(Student student);
    
    /** Batch insert multiple students (used for CSV upload) */
    void addStudentsBatch(List<Student> students);
    
    /** Get all students in the system */
    List<Student> getAllStudents();
    
    /** Find a student by roll number */
    Student getStudentByRollNo(String rollNo);
    
    /** Get student by ID */
    Student getStudentById(int studentId);
    
    /** Delete a student by ID */
    void deleteStudent(int studentId);
    
    /** Get total count of students */
    int getStudentCount();
    
    /** Delete all students */
    void deleteAllStudents();
}
