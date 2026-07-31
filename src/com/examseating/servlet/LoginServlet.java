package com.examseating.servlet;

import com.examseating.dao.AdminDAO;
import com.examseating.dao.AdminDAOImpl;
import com.examseating.model.AdminUser;
import com.examseating.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet - Handles admin authentication.
 * 
 * GET  /login → Show login form
 * POST /login → Authenticate and create session
 */
public class LoginServlet extends HttpServlet {

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMsg", "Username and password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Hash the password and check against database
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        try {
            AdminUser admin = adminDAO.authenticate(username.trim(), hashedPassword);
            
            if (admin != null) {
                // Authentication successful — create session
                HttpSession session = request.getSession(true);
                session.setAttribute("adminUser", admin.getUsername());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                request.setAttribute("errorMsg", "Invalid username or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMsg", "Login error: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
