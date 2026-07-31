package com.examseating.servlet;

import com.examseating.algorithm.SeatAllocator;
import com.examseating.dao.*;
import com.examseating.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AllocateSeatsServlet - Triggers the seating allocation algorithm.
 * 
 * GET  /admin/allocate → Show form to select exam + rooms
 * POST /admin/allocate → Run allocation algorithm and redirect to chart
 */
public class AllocateSeatsServlet extends HttpServlet {

    private ExamSessionDAO examDAO;
    private RoomDAO roomDAO;
    private StudentDAO studentDAO;
    private AllocationDAO allocationDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamSessionDAOImpl();
        roomDAO = new RoomDAOImpl();
        studentDAO = new StudentDAOImpl();
        allocationDAO = new AllocationDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Load data for the allocation form
        List<ExamSession> exams = examDAO.getAllExams();
        List<Room> rooms = roomDAO.getAllRooms();
        List<Student> students = studentDAO.getAllStudents();
        
        request.setAttribute("exams", exams);
        request.setAttribute("rooms", rooms);
        request.setAttribute("studentCount", students.size());
        
        // Check which exams already have allocations
        for (ExamSession exam : exams) {
            if (allocationDAO.hasAllocations(exam.getExamId())) {
                request.setAttribute("hasAllocation_" + exam.getExamId(), true);
            }
        }
        
        request.getRequestDispatcher("/allocate-seats.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String examIdStr = request.getParameter("examId");
        String[] roomIdStrs = request.getParameterValues("roomIds");
        
        // Validation
        if (examIdStr == null || examIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Please select an exam session.");
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
            return;
        }
        
        if (roomIdStrs == null || roomIdStrs.length == 0) {
            request.getSession().setAttribute("errorMsg", "Please select at least one room.");
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
            return;
        }
        
        int examId;
        try {
            examId = Integer.parseInt(examIdStr);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Invalid exam ID.");
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
            return;
        }
        
        // Fetch selected rooms
        List<Room> selectedRooms = new ArrayList<>();
        for (String rid : roomIdStrs) {
            try {
                Room room = roomDAO.getRoomById(Integer.parseInt(rid));
                if (room != null) selectedRooms.add(room);
            } catch (NumberFormatException e) {
                // Skip invalid room IDs
            }
        }
        
        // Fetch all students
        List<Student> students = studentDAO.getAllStudents();
        
        if (students.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "No students found. Upload students first.");
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
            return;
        }
        
        // Calculate total capacity
        int totalCapacity = 0;
        for (Room r : selectedRooms) {
            totalCapacity += r.getCapacity();
        }
        
        if (totalCapacity < students.size()) {
            request.getSession().setAttribute("errorMsg", 
                "Not enough room capacity! Need " + students.size() + " seats but only " 
                + totalCapacity + " available. Select more rooms.");
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
            return;
        }
        
        try {
            // Delete existing allocations for this exam (in case of re-run)
            allocationDAO.deleteAllocationsByExam(examId);
            
            // Run the allocation algorithm
            SeatAllocator allocator = new SeatAllocator();
            List<SeatAllocation> allocations = allocator.allocate(students, selectedRooms, examId);
            
            // Save results to database
            if (!allocations.isEmpty()) {
                allocationDAO.saveAllocations(allocations);
            }
            
            request.getSession().setAttribute("successMsg", 
                "Allocation complete! " + allocations.size() + " students seated across " 
                + selectedRooms.size() + " room(s).");
            
            // Redirect to seating chart view
            response.sendRedirect(request.getContextPath() + "/admin/seating-chart?examId=" + examId);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Allocation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/allocate");
        }
    }
}
