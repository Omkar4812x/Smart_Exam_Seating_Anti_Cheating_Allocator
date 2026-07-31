package com.examseating.servlet;

import com.examseating.dao.RoomDAO;
import com.examseating.dao.RoomDAOImpl;
import com.examseating.model.Room;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * AddRoomServlet - Handles CRUD operations for exam rooms.
 * 
 * GET  /admin/rooms         → Display room list + add form
 * POST /admin/rooms          → Add or update a room
 * GET  /admin/rooms?action=delete&id=X → Delete a room
 * GET  /admin/rooms?action=edit&id=X   → Load room for editing
 */
public class AddRoomServlet extends HttpServlet {

    private RoomDAO roomDAO;

    @Override
    public void init() throws ServletException {
        roomDAO = new RoomDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            // Delete room
            try {
                int roomId = Integer.parseInt(request.getParameter("id"));
                roomDAO.deleteRoom(roomId);
                request.getSession().setAttribute("successMsg", "Room deleted successfully.");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Failed to delete room: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
            
        } else if ("edit".equals(action)) {
            // Load room for editing
            try {
                int roomId = Integer.parseInt(request.getParameter("id"));
                Room room = roomDAO.getRoomById(roomId);
                if (room != null) {
                    request.setAttribute("editRoom", room);
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Room not found.");
            }
        }
        
        // Load all rooms for display
        List<Room> rooms = roomDAO.getAllRooms();
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/add-room.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Read form parameters
        String roomNo = request.getParameter("roomNo");
        String rowsStr = request.getParameter("rowsCount");
        String colsStr = request.getParameter("colsCount");
        String roomIdStr = request.getParameter("roomId");
        
        // Server-side validation
        if (roomNo == null || roomNo.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Room number is required.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }
        
        int rowsCount, colsCount;
        try {
            rowsCount = Integer.parseInt(rowsStr);
            colsCount = Integer.parseInt(colsStr);
            if (rowsCount <= 0 || colsCount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Rows and columns must be positive integers.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }
        
        try {
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                // UPDATE existing room
                Room room = new Room();
                room.setRoomId(Integer.parseInt(roomIdStr));
                room.setRoomNo(roomNo.trim());
                room.setRowsCount(rowsCount);
                room.setColsCount(colsCount);
                roomDAO.updateRoom(room);
                request.getSession().setAttribute("successMsg", "Room updated successfully.");
            } else {
                // INSERT new room
                Room room = new Room();
                room.setRoomNo(roomNo.trim());
                room.setRowsCount(rowsCount);
                room.setColsCount(colsCount);
                roomDAO.addRoom(room);
                request.getSession().setAttribute("successMsg", "Room added successfully.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error saving room: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }
}
