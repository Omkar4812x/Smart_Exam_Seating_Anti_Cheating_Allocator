package com.examseating.servlet;

import com.examseating.algorithm.DutyScheduler;
import com.examseating.dao.*;
import com.examseating.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * AssignInvigilatorsServlet - Manages invigilators and triggers duty auto-assignment.
 * 
 * GET  /admin/invigilators → List invigilators + add form + duty schedule
 * POST /admin/invigilators → Add invigilator or run auto-assign
 */
public class AssignInvigilatorsServlet extends HttpServlet {

    private InvigilatorDAO invigilatorDAO;
    private ExamSessionDAO examDAO;
    private RoomDAO roomDAO;
    private DutyScheduleDAO dutyScheduleDAO;
    private AllocationDAO allocationDAO;

    @Override
    public void init() throws ServletException {
        invigilatorDAO = new InvigilatorDAOImpl();
        examDAO = new ExamSessionDAOImpl();
        roomDAO = new RoomDAOImpl();
        dutyScheduleDAO = new DutyScheduleDAOImpl();
        allocationDAO = new AllocationDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                invigilatorDAO.deleteInvigilator(id);
                request.getSession().setAttribute("successMsg", "Invigilator deleted.");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMsg", "Failed to delete invigilator.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/invigilators");
            return;
        }
        
        loadPageData(request);
        request.getRequestDispatcher("/invigilator-assign.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String formAction = request.getParameter("formAction");
        
        if ("addInvigilator".equals(formAction)) {
            handleAddInvigilator(request);
        } else if ("autoAssign".equals(formAction)) {
            handleAutoAssign(request);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/invigilators");
    }

    private void handleAddInvigilator(HttpServletRequest request) {
        String name = request.getParameter("invName");
        String dept = request.getParameter("invDepartment");
        
        if (name == null || name.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Invigilator name is required.");
            return;
        }
        
        Invigilator inv = new Invigilator();
        inv.setName(name.trim());
        inv.setDepartment(dept != null ? dept.trim() : "");
        
        try {
            invigilatorDAO.addInvigilator(inv);
            request.getSession().setAttribute("successMsg", "Invigilator added: " + name);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Failed to add invigilator: " + e.getMessage());
        }
    }

    private void handleAutoAssign(HttpServletRequest request) {
        String examIdStr = request.getParameter("examId");
        
        if (examIdStr == null || examIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Please select an exam for duty assignment.");
            return;
        }
        
        try {
            int examId = Integer.parseInt(examIdStr);
            ExamSession exam = examDAO.getExamById(examId);
            
            if (exam == null) {
                request.getSession().setAttribute("errorMsg", "Exam not found.");
                return;
            }
            
            // Get rooms that have allocations for this exam
            List<SeatAllocation> allocs = allocationDAO.getAllocationsByExam(examId);
            if (allocs.isEmpty()) {
                request.getSession().setAttribute("errorMsg", "No seating allocation found for this exam. Run allocation first.");
                return;
            }
            
            // Get unique rooms from allocations
            java.util.Set<Integer> roomIdSet = new java.util.LinkedHashSet<>();
            for (SeatAllocation a : allocs) roomIdSet.add(a.getRoomId());
            
            java.util.List<Room> rooms = new java.util.ArrayList<>();
            for (int rid : roomIdSet) {
                Room r = roomDAO.getRoomById(rid);
                if (r != null) rooms.add(r);
            }
            
            List<Invigilator> invigilators = invigilatorDAO.getAllInvigilators();
            
            if (invigilators.isEmpty()) {
                request.getSession().setAttribute("errorMsg", "No invigilators available. Add invigilators first.");
                return;
            }
            
            // Delete existing duties for this exam
            dutyScheduleDAO.deleteDutiesByExam(examId);
            
            // Run the duty scheduler
            DutyScheduler scheduler = new DutyScheduler();
            String dutySlot = exam.getExamTime() != null ? exam.getExamTime() : "Full Day";
            List<DutySchedule> duties = scheduler.assignDuties(rooms, invigilators, examId, dutySlot);
            
            if (!duties.isEmpty()) {
                dutyScheduleDAO.saveDuties(duties);
            }
            
            request.getSession().setAttribute("successMsg", 
                "Auto-assigned " + duties.size() + " invigilator duties for " + exam.getExamName());
            
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Auto-assign failed: " + e.getMessage());
        }
    }

    private void loadPageData(HttpServletRequest request) {
        List<Invigilator> invigilators = invigilatorDAO.getAllInvigilators();
        List<ExamSession> exams = examDAO.getAllExams();
        
        request.setAttribute("invigilators", invigilators);
        request.setAttribute("exams", exams);
        
        // Load duty schedule for selected exam
        String examIdStr = request.getParameter("examId");
        if (examIdStr != null && !examIdStr.isEmpty()) {
            try {
                int examId = Integer.parseInt(examIdStr);
                List<DutySchedule> duties = dutyScheduleDAO.getDutiesByExam(examId);
                request.setAttribute("duties", duties);
                request.setAttribute("selectedExamId", examId);
            } catch (NumberFormatException e) { /* ignore */ }
        }
        
        // Flash messages
        String successMsg = (String) request.getSession().getAttribute("successMsg");
        String errorMsg = (String) request.getSession().getAttribute("errorMsg");
        if (successMsg != null) { request.setAttribute("successMsg", successMsg); request.getSession().removeAttribute("successMsg"); }
        if (errorMsg != null) { request.setAttribute("errorMsg", errorMsg); request.getSession().removeAttribute("errorMsg"); }
    }
}
