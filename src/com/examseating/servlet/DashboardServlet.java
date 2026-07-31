package com.examseating.servlet;

import com.examseating.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DashboardServlet - Loads dashboard statistics and forwards to JSP.
 */
public class DashboardServlet extends HttpServlet {

    private ExamSessionDAO examDAO;
    private StudentDAO studentDAO;
    private RoomDAO roomDAO;
    private InvigilatorDAO invigilatorDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamSessionDAOImpl();
        studentDAO = new StudentDAOImpl();
        roomDAO = new RoomDAOImpl();
        invigilatorDAO = new InvigilatorDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("totalExams", examDAO.getAllExams().size());
        request.setAttribute("totalStudents", studentDAO.getStudentCount());
        request.setAttribute("totalRooms", roomDAO.getAllRooms().size());
        request.setAttribute("totalInvigilators", invigilatorDAO.getInvigilatorCount());
        
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }
}
