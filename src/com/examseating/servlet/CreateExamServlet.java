package com.examseating.servlet;

import com.examseating.dao.ExamSessionDAO;
import com.examseating.dao.ExamSessionDAOImpl;
import com.examseating.model.ExamSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * CreateExamServlet - Handles CRUD for exam sessions.
 * 
 * GET  /admin/exams          → List exams + create form
 * POST /admin/exams           → Create new exam session
 * GET  /admin/exams?action=delete&id=X → Delete exam
 */
public class CreateExamServlet extends HttpServlet {

    private ExamSessionDAO examDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamSessionDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            try {
                int examId = Integer.parseInt(request.getParameter("id"));
                examDAO.deleteExam(examId);
                request.getSession().setAttribute("successMsg", "Exam session deleted.");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Failed to delete exam: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/admin/exams");
            return;
        }
        
        List<ExamSession> exams = examDAO.getAllExams();
        request.setAttribute("exams", exams);
        request.getRequestDispatcher("/create-exam.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String examName = request.getParameter("examName");
        String examDate = request.getParameter("examDate");
        String examTime = request.getParameter("examTime");
        
        // Server-side validation
        if (examName == null || examName.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Exam name is required.");
            response.sendRedirect(request.getContextPath() + "/admin/exams");
            return;
        }
        if (examDate == null || examDate.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Exam date is required.");
            response.sendRedirect(request.getContextPath() + "/admin/exams");
            return;
        }
        
        try {
            ExamSession exam = new ExamSession();
            exam.setExamName(examName.trim());
            exam.setExamDate(examDate.trim());
            exam.setExamTime(examTime != null ? examTime.trim() : "");
            examDAO.addExam(exam);
            request.getSession().setAttribute("successMsg", "Exam session created successfully.");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error creating exam: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/exams");
    }
}
