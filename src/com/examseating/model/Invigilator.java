package com.examseating.model;

/**
 * Invigilator - Represents a staff member who can supervise exams.
 */
public class Invigilator {

    private int invigilatorId;
    private String name;
    private String department;

    public Invigilator() {}

    public Invigilator(int invigilatorId, String name, String department) {
        this.invigilatorId = invigilatorId;
        this.name = name;
        this.department = department;
    }

    public int getInvigilatorId() { return invigilatorId; }
    public void setInvigilatorId(int invigilatorId) { this.invigilatorId = invigilatorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Invigilator{" + name + ", " + department + "}";
    }
}
