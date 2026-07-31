package com.examseating.model;

/**
 * DutySchedule - Represents an invigilator's duty assignment for a specific
 * exam room and time slot.
 */
public class DutySchedule {

    private int dutyId;
    private int examId;
    private int roomId;
    private int invigilatorId;
    private String dutySlot;
    
    // Transient display fields
    private String invigilatorName;
    private String department;
    private String roomNo;
    private String examName;

    public DutySchedule() {}

    // Getters and Setters
    public int getDutyId() { return dutyId; }
    public void setDutyId(int dutyId) { this.dutyId = dutyId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getInvigilatorId() { return invigilatorId; }
    public void setInvigilatorId(int invigilatorId) { this.invigilatorId = invigilatorId; }

    public String getDutySlot() { return dutySlot; }
    public void setDutySlot(String dutySlot) { this.dutySlot = dutySlot; }

    public String getInvigilatorName() { return invigilatorName; }
    public void setInvigilatorName(String invigilatorName) { this.invigilatorName = invigilatorName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
}
