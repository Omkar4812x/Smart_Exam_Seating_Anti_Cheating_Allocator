package com.examseating.dao;

import com.examseating.model.Room;
import java.util.List;

/**
 * RoomDAO - Data Access Object interface for Room entity.
 * Defines CRUD operations for exam rooms.
 */
public interface RoomDAO {
    
    /** Insert a new room into the database */
    void addRoom(Room room);
    
    /** Update an existing room */
    void updateRoom(Room room);
    
    /** Delete a room by its ID */
    void deleteRoom(int roomId);
    
    /** Retrieve a single room by ID */
    Room getRoomById(int roomId);
    
    /** Retrieve all rooms */
    List<Room> getAllRooms();
}
