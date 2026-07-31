package com.examseating.servlet;

import com.examseating.dao.*;
import com.examseating.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * ViewSeatingChartServlet - Displays the visual seating chart for an exam.
 * 
 * GET /admin/seating-chart?examId=X → Show seating grids for all rooms
 */
public class ViewSeatingChartServlet extends HttpServlet {

    private ExamSessionDAO examDAO;
    private RoomDAO roomDAO;
    private AllocationDAO allocationDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamSessionDAOImpl();
        roomDAO = new RoomDAOImpl();
        allocationDAO = new AllocationDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String examIdStr = request.getParameter("examId");
        
        // Load all exams for the dropdown selector
        List<ExamSession> exams = examDAO.getAllExams();
        request.setAttribute("exams", exams);
        
        if (examIdStr != null && !examIdStr.isEmpty()) {
            try {
                int examId = Integer.parseInt(examIdStr);
                ExamSession exam = examDAO.getExamById(examId);
                request.setAttribute("selectedExam", exam);
                
                // Fetch all allocations for this exam
                List<SeatAllocation> allocations = allocationDAO.getAllocationsByExam(examId);
                
                if (!allocations.isEmpty()) {
                    // Group allocations by room
                    Map<Integer, List<SeatAllocation>> roomAllocations = new LinkedHashMap<>();
                    Map<Integer, String> roomNames = new LinkedHashMap<>();
                    Set<String> subjects = new LinkedHashSet<>();
                    
                    for (SeatAllocation alloc : allocations) {
                        roomAllocations.computeIfAbsent(alloc.getRoomId(), k -> new ArrayList<>()).add(alloc);
                        roomNames.put(alloc.getRoomId(), alloc.getRoomNo());
                        subjects.add(alloc.getSubjectCode());
                    }
                    
                    // Build room grid data for JSP
                    // For each room, we need: room info + 2D grid of allocations
                    Map<Integer, Room> roomMap = new LinkedHashMap<>();
                    for (Map.Entry<Integer, List<SeatAllocation>> entry : roomAllocations.entrySet()) {
                        Room room = roomDAO.getRoomById(entry.getKey());
                        if (room != null) {
                            roomMap.put(entry.getKey(), room);
                        }
                    }
                    
                    request.setAttribute("roomAllocations", roomAllocations);
                    request.setAttribute("roomNames", roomNames);
                    request.setAttribute("roomMap", roomMap);
                    request.setAttribute("subjects", subjects);
                    request.setAttribute("totalAllocations", allocations.size());
                }
                
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMsg", "Invalid exam ID.");
            }
        }
        
        String successMsg = (String) request.getSession().getAttribute("successMsg");
        if (successMsg != null) request.getSession().removeAttribute("successMsg");
        request.setAttribute("successMsg", successMsg);
        
        request.getRequestDispatcher("/seating-chart.jsp").forward(request, response);
    }
}
