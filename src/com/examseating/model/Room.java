package com.examseating.model;

/**
 * Room - Represents an exam room/hall in the system.
 * 
 * Each room has a grid layout defined by rowsCount x colsCount.
 * The capacity is automatically calculated as rowsCount * colsCount.
 */
public class Room {

    private int roomId;
    private String roomNo;
    private int rowsCount;
    private int colsCount;
    private int capacity;

    public Room() {}

    public Room(int roomId, String roomNo, int rowsCount, int colsCount) {
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
        this.capacity = rowsCount * colsCount;
    }

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public int getRowsCount() { return rowsCount; }
    public void setRowsCount(int rowsCount) { 
        this.rowsCount = rowsCount;
        this.capacity = this.rowsCount * this.colsCount;
    }

    public int getColsCount() { return colsCount; }
    public void setColsCount(int colsCount) { 
        this.colsCount = colsCount;
        this.capacity = this.rowsCount * this.colsCount;
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "Room{" + roomNo + ", " + rowsCount + "x" + colsCount + ", cap=" + capacity + "}";
    }
}
