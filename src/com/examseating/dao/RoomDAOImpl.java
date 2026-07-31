package com.examseating.dao;

import com.examseating.model.Room;
import com.examseating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAOImpl - JDBC implementation of RoomDAO.
 * Uses PreparedStatement and try-with-resources for safe resource management.
 */
public class RoomDAOImpl implements RoomDAO {

    @Override
    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_no, rows_count, cols_count, capacity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, room.getRoomNo());
            ps.setInt(2, room.getRowsCount());
            ps.setInt(3, room.getColsCount());
            ps.setInt(4, room.getRowsCount() * room.getColsCount());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error adding room: " + e.getMessage());
            throw new RuntimeException("Failed to add room", e);
        }
    }

    @Override
    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_no=?, rows_count=?, cols_count=?, capacity=? WHERE room_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, room.getRoomNo());
            ps.setInt(2, room.getRowsCount());
            ps.setInt(3, room.getColsCount());
            ps.setInt(4, room.getRowsCount() * room.getColsCount());
            ps.setInt(5, room.getRoomId());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error updating room: " + e.getMessage());
            throw new RuntimeException("Failed to update room", e);
        }
    }

    @Override
    public void deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE room_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error deleting room: " + e.getMessage());
            throw new RuntimeException("Failed to delete room", e);
        }
    }

    @Override
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error fetching room: " + e.getMessage());
            throw new RuntimeException("Failed to fetch room", e);
        }
        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_no";
        List<Room> rooms = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error fetching rooms: " + e.getMessage());
            throw new RuntimeException("Failed to fetch rooms", e);
        }
        return rooms;
    }

    /**
     * Maps a ResultSet row to a Room object.
     */
    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNo(rs.getString("room_no"));
        room.setRowsCount(rs.getInt("rows_count"));
        room.setColsCount(rs.getInt("cols_count"));
        room.setCapacity(rs.getInt("capacity"));
        return room;
    }
}
