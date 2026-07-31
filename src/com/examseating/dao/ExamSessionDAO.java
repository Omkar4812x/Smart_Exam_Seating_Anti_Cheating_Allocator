package com.examseating.dao;

import com.examseating.model.ExamSession;
import java.util.List;

/**
 * ExamSessionDAO - Data Access Object interface for ExamSession entity.
 */
public interface ExamSessionDAO {
    
    void addExam(ExamSession exam);
    
    ExamSession getExamById(int examId);
    
    List<ExamSession> getAllExams();
    
    void deleteExam(int examId);
}
