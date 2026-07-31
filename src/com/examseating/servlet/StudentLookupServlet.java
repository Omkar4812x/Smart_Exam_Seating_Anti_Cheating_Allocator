package com.examseating.servlet;

import com.examseating.dao.*;
import com.examseating.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * StudentLookupServlet - Public-facing page for students to find their seat.
 * No login required.
 * 
 * GET  /student-lookup → Show search form
 * POST /student-lookup → Search by roll number and exam
 */
public class StudentLookupServlet extends HttpServlet {

    private ExamSessionDAO examDAO;
    private AllocationDAO allocationDAO;
    private RoomDAO roomDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamSessionDAOImpl();
        allocationDAO = new AllocationDAOImpl();
        roomDAO = new RoomDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<ExamSession> exams = examDAO.getAllExams();
        request.setAttribute("exams", exams);
        request.getRequestDispatcher("/student-lookup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String rollNo = request.getParameter("rollNo");
        String examIdStr = request.getParameter("examId");
        
        List<ExamSession> exams = examDAO.getAllExams();
        request.setAttribute("exams", exams);
        request.setAttribute("searchRollNo", rollNo);
        request.setAttribute("searchExamId", examIdStr);
        
        if (rollNo == null || rollNo.trim().isEmpty()) {
            request.setAttribute("errorMsg", "Please enter your roll number.");
            request.getRequestDispatcher("/student-lookup.jsp").forward(request, response);
            return;
        }
        
        if (examIdStr == null || examIdStr.isEmpty()) {
            request.setAttribute("errorMsg", "Please select an exam.");
            request.getRequestDispatcher("/student-lookup.jsp").forward(request, response);
            return;
        }
        
        try {
            int examId = Integer.parseInt(examIdStr);
            ExamSession exam = examDAO.getExamById(examId);
            SeatAllocation allocation = allocationDAO.findByRollNoAndExam(rollNo.trim(), examId);
            
            if (allocation != null) {
                Room room = roomDAO.getRoomById(allocation.getRoomId());
                request.setAttribute("result", allocation);
                request.setAttribute("resultExam", exam);
                request.setAttribute("resultRoom", room);
            } else {
                request.setAttribute("errorMsg", 
                    "No seat allocation found for roll number '" + rollNo.trim() + "' in the selected exam. " +
                    "Please check your roll number or contact the exam coordinator.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMsg", "Invalid exam selection.");
        } catch (Exception e) {
            request.setAttribute("errorMsg", "An error occurred: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/student-lookup.jsp").forward(request, response);
    }
}
