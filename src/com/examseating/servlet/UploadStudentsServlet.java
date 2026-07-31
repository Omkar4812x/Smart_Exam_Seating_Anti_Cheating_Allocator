package com.examseating.servlet;

import com.examseating.dao.StudentDAO;
import com.examseating.dao.StudentDAOImpl;
import com.examseating.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * UploadStudentsServlet - Handles single student add and CSV bulk upload.
 * 
 * GET  /admin/students        → Display student list + upload forms
 * POST /admin/students         → Add single student or process CSV upload
 * GET  /admin/students?action=delete&id=X → Delete a student
 * 
 * CSV Format (no header row required, but if present first row is skipped):
 *   roll_no, name, branch, class_year, subject_code
 */
public class UploadStudentsServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            try {
                int studentId = Integer.parseInt(request.getParameter("id"));
                studentDAO.deleteStudent(studentId);
                request.getSession().setAttribute("successMsg", "Student deleted.");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Failed to delete student.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/students");
            return;
        }
        
        if ("deleteAll".equals(action)) {
            try {
                studentDAO.deleteAllStudents();
                request.getSession().setAttribute("successMsg", "All students deleted.");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Failed to delete students.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/students");
            return;
        }
        
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        request.getRequestDispatcher("/upload-students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String uploadType = request.getParameter("uploadType");
        
        if ("csv".equals(uploadType)) {
            handleCsvUpload(request, response);
        } else {
            handleSingleUpload(request, response);
        }
    }

    /**
     * Handles single student form submission.
     */
    private void handleSingleUpload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String rollNo = request.getParameter("rollNo");
        String name = request.getParameter("studentName");
        String branch = request.getParameter("branch");
        String classYear = request.getParameter("classYear");
        String subjectCode = request.getParameter("subjectCode");
        
        // Validation
        if (rollNo == null || rollNo.trim().isEmpty() || 
            subjectCode == null || subjectCode.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Roll Number and Subject Code are required.");
            response.sendRedirect(request.getContextPath() + "/admin/students");
            return;
        }
        
        try {
            Student student = new Student();
            student.setRollNo(rollNo.trim());
            student.setName(name != null ? name.trim() : "");
            student.setBranch(branch != null ? branch.trim() : "");
            student.setClassYear(classYear != null ? classYear.trim() : "");
            student.setSubjectCode(subjectCode.trim());
            
            studentDAO.addStudent(student);
            request.getSession().setAttribute("successMsg", "Student added: " + rollNo);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error adding student: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/students");
    }

    /**
     * Handles CSV file upload and batch insert.
     * Expected CSV format: roll_no, name, branch, class_year, subject_code
     * First row is auto-detected as header if it contains "roll" (case-insensitive).
     */
    private void handleCsvUpload(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        Part filePart = request.getPart("csvFile");
        
        if (filePart == null || filePart.getSize() == 0) {
            request.getSession().setAttribute("errorMsg", "Please select a CSV file to upload.");
            response.sendRedirect(request.getContextPath() + "/admin/students");
            return;
        }
        
        List<Student> students = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int lineNum = 0;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), "UTF-8"))) {
            String line;
            boolean headerSkipped = false;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) continue;
                
                // Auto-detect and skip header row
                if (!headerSkipped && line.toLowerCase().contains("roll")) {
                    headerSkipped = true;
                    continue;
                }
                headerSkipped = true;
                
                // Parse CSV line
                String[] fields = line.split(",", -1);
                
                if (fields.length < 5) {
                    errors.add("Line " + lineNum + ": Expected 5 fields, got " + fields.length);
                    continue;
                }
                
                String rollNo = fields[0].trim();
                String name = fields[1].trim();
                String branch = fields[2].trim();
                String classYear = fields[3].trim();
                String subjectCode = fields[4].trim();
                
                if (rollNo.isEmpty() || subjectCode.isEmpty()) {
                    errors.add("Line " + lineNum + ": Roll number and subject code cannot be empty");
                    continue;
                }
                
                Student student = new Student();
                student.setRollNo(rollNo);
                student.setName(name);
                student.setBranch(branch);
                student.setClassYear(classYear);
                student.setSubjectCode(subjectCode);
                students.add(student);
            }
        }
        
        if (!students.isEmpty()) {
            try {
                studentDAO.addStudentsBatch(students);
                String msg = "Successfully uploaded " + students.size() + " students.";
                if (!errors.isEmpty()) {
                    msg += " (" + errors.size() + " rows skipped due to errors)";
                }
                request.getSession().setAttribute("successMsg", msg);
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Database error during upload: " + e.getMessage());
            }
        } else {
            String msg = "No valid student records found in CSV.";
            if (!errors.isEmpty()) {
                msg += " Errors: " + String.join("; ", errors.subList(0, Math.min(5, errors.size())));
            }
            request.getSession().setAttribute("errorMsg", msg);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/students");
    }
}
