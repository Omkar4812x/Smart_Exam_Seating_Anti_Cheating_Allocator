package com.examseating.model;

/**
 * SeatAllocation - Represents a single seat assignment in the allocation result.
 * 
 * Maps a student to a specific seat position (row, col) in a room for an exam.
 * Transient display fields (rollNo, subjectCode, studentName, roomNo) are populated
 * when fetching allocations for the seating chart view.
 */
public class SeatAllocation {

    private int allocationId;
    private int examId;
    private int studentId;
    private int roomId;
    private int seatRow;
    private int seatCol;
    
    // Transient display fields (not stored, populated via JOINs for views)
    private String rollNo;
    private String subjectCode;
    private String studentName;
    private String roomNo;
    private String classYear;
    private String branch;

    public SeatAllocation() {}

    public SeatAllocation(int examId, int studentId, int roomId, int seatRow, int seatCol) {
        this.examId = examId;
        this.studentId = studentId;
        this.roomId = roomId;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    // Getters and Setters
    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getSeatRow() { return seatRow; }
    public void setSeatRow(int seatRow) { this.seatRow = seatRow; }

    public int getSeatCol() { return seatCol; }
    public void setSeatCol(int seatCol) { this.seatCol = seatCol; }

    // Transient getters/setters
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public String getClassYear() { return classYear; }
    public void setClassYear(String classYear) { this.classYear = classYear; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}
