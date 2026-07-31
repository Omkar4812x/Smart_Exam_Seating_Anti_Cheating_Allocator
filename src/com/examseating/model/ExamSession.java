package com.examseating.model;

/**
 * ExamSession - Represents a single exam event/session.
 * 
 * Each exam session has a name, date, and time slot.
 * Students are allocated to rooms for a specific exam session.
 */
public class ExamSession {

    private int examId;
    private String examName;
    private String examDate;   // stored as String for JSP convenience (format: yyyy-MM-dd)
    private String examTime;

    public ExamSession() {}

    public ExamSession(int examId, String examName, String examDate, String examTime) {
        this.examId = examId;
        this.examName = examName;
        this.examDate = examDate;
        this.examTime = examTime;
    }

    // Getters and Setters
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }

    public String getExamTime() { return examTime; }
    public void setExamTime(String examTime) { this.examTime = examTime; }

    @Override
    public String toString() {
        return "ExamSession{" + examName + ", " + examDate + " " + examTime + "}";
    }
}
