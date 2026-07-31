<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.ExamSession" %>
<%@ page import="java.util.List" %>
<%
    List<ExamSession> exams = (List<ExamSession>) request.getAttribute("exams");
    
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Exams - Exam Seating Allocator</title>
    <meta name="description" content="Create and manage exam sessions for seating allocation">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Outfit:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="app-layout">
        <%@ include file="sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title"><i class="bi bi-calendar-event"></i> Manage Exams</h1>
                <p class="page-subtitle">Create and schedule exam sessions</p>
            </div>
            
            <% if (successMsg != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i><%= successMsg %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMsg %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>
            
            <div class="row g-4">
                <!-- Create Exam Form -->
                <div class="col-lg-5">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-plus-circle"></i> Create Exam Session
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/exams" method="post" id="examForm" onsubmit="return validateExamForm()">
                                <div class="form-group-custom">
                                    <label for="examName" class="form-label-custom">Exam Name</label>
                                    <input type="text" class="form-control-custom" id="examName" name="examName" 
                                           placeholder="e.g., Mid-Semester Exam 2026" required>
                                </div>
                                
                                <div class="form-group-custom">
                                    <label for="examDate" class="form-label-custom">Exam Date</label>
                                    <input type="date" class="form-control-custom" id="examDate" name="examDate" required>
                                </div>
                                
                                <div class="form-group-custom">
                                    <label for="examTime" class="form-label-custom">Time Slot</label>
                                    <input type="text" class="form-control-custom" id="examTime" name="examTime" 
                                           placeholder="e.g., 10:00 AM - 01:00 PM">
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-calendar-plus"></i> Create Exam
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <!-- Exam List -->
                <div class="col-lg-7">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-list-ul"></i> Exam Sessions
                                <span class="badge-count"><%= exams != null ? exams.size() : 0 %></span>
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <% if (exams == null || exams.isEmpty()) { %>
                                <div class="empty-state">
                                    <i class="bi bi-calendar-x empty-icon"></i>
                                    <h3>No exams created yet</h3>
                                    <p>Create your first exam session using the form on the left.</p>
                                </div>
                            <% } else { %>
                                <div class="table-responsive">
                                    <table class="table-custom">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Exam Name</th>
                                                <th>Date</th>
                                                <th>Time</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% for (ExamSession e : exams) { %>
                                                <tr>
                                                    <td><%= e.getExamId() %></td>
                                                    <td><strong><%= e.getExamName() %></strong></td>
                                                    <td><span class="date-badge"><%= e.getExamDate() %></span></td>
                                                    <td><%= e.getExamTime() %></td>
                                                    <td>
                                                        <div class="action-btns">
                                                            <a href="<%=request.getContextPath()%>/admin/exams?action=delete&id=<%= e.getExamId() %>" 
                                                               class="btn-icon delete" title="Delete"
                                                               onclick="return confirm('Delete this exam session?')">
                                                                <i class="bi bi-trash3"></i>
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function validateExamForm() {
            const name = document.getElementById('examName').value.trim();
            const date = document.getElementById('examDate').value;
            if (!name) { alert('Exam name is required.'); return false; }
            if (!date) { alert('Exam date is required.'); return false; }
            return true;
        }
    </script>
</body>
</html>
