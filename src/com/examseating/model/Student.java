package com.examseating.model;

/**
 * Student - Represents a student enrolled for an exam.
 * 
 * Key fields for the seating algorithm:
 *   - subjectCode: used as the primary constraint (no same-subject neighbors)
 *   - classYear: used as a secondary constraint (ideally no same-class neighbors)
 */
public class Student {

    private int studentId;
    private String rollNo;
    private String name;
    private String branch;
    private String classYear;
    private String subjectCode;

    public Student() {}

    public Student(int studentId, String rollNo, String name, String branch, 
                   String classYear, String subjectCode) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.name = name;
        this.branch = branch;
        this.classYear = classYear;
        this.subjectCode = subjectCode;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getClassYear() { return classYear; }
    public void setClassYear(String classYear) { this.classYear = classYear; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    @Override
    public String toString() {
        return "Student{" + rollNo + ", " + name + ", " + subjectCode + "}";
    }
}
