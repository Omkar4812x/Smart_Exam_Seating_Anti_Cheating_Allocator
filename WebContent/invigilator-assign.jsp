<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.*" %>
<%@ page import="java.util.*" %>
<%
    List<Invigilator> invigilators = (List<Invigilator>) request.getAttribute("invigilators");
    List<ExamSession> exams = (List<ExamSession>) request.getAttribute("exams");
    List<DutySchedule> duties = (List<DutySchedule>) request.getAttribute("duties");
    Integer selectedExamId = (Integer) request.getAttribute("selectedExamId");
    
    String successMsg = (String) request.getAttribute("successMsg");
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invigilators - Exam Seating Allocator</title>
    <meta name="description" content="Manage invigilators and auto-assign exam duties">
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
                <h1 class="page-title"><i class="bi bi-person-badge"></i> Invigilators</h1>
                <p class="page-subtitle">Manage invigilators and auto-assign exam duties</p>
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
                <!-- Add Invigilator Form -->
                <div class="col-lg-4">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom"><i class="bi bi-person-plus"></i> Add Invigilator</h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/invigilators" method="post">
                                <input type="hidden" name="formAction" value="addInvigilator">
                                <div class="form-group-custom">
                                    <label for="invName" class="form-label-custom">Name *</label>
                                    <input type="text" class="form-control-custom" id="invName" name="invName" 
                                           placeholder="e.g., Dr. Ramesh Kumar" required>
                                </div>
                                <div class="form-group-custom">
                                    <label for="invDepartment" class="form-label-custom">Department</label>
                                    <input type="text" class="form-control-custom" id="invDepartment" name="invDepartment" 
                                           placeholder="e.g., CSE">
                                </div>
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-person-plus"></i> Add
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Auto-Assign -->
                    <div class="glass-card mt-4">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom"><i class="bi bi-magic"></i> Auto-Assign Duties</h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/invigilators" method="post">
                                <input type="hidden" name="formAction" value="autoAssign">
                                <div class="form-group-custom">
                                    <label for="examIdAssign" class="form-label-custom">Select Exam</label>
                                    <select class="form-control-custom" id="examIdAssign" name="examId" required>
                                        <option value="">-- Choose exam --</option>
                                        <% if (exams != null) { for (ExamSession e : exams) { %>
                                            <option value="<%= e.getExamId() %>"><%= e.getExamName() %></option>
                                        <% } } %>
                                    </select>
                                </div>
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-lightning"></i> Auto-Assign
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <!-- Invigilator List + Duty Table -->
                <div class="col-lg-8">
                    <!-- Invigilator List -->
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-people"></i> Invigilators
                                <span class="badge-count"><%= invigilators != null ? invigilators.size() : 0 %></span>
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <% if (invigilators == null || invigilators.isEmpty()) { %>
                                <div class="empty-state">
                                    <i class="bi bi-person-badge empty-icon"></i>
                                    <h3>No invigilators added</h3>
                                    <p>Add invigilators using the form on the left.</p>
                                </div>
                            <% } else { %>
                                <div class="table-responsive">
                                    <table class="table-custom">
                                        <thead>
                                            <tr><th>#</th><th>Name</th><th>Department</th><th>Actions</th></tr>
                                        </thead>
                                        <tbody>
                                            <% for (Invigilator inv : invigilators) { %>
                                                <tr>
                                                    <td><%= inv.getInvigilatorId() %></td>
                                                    <td><strong><%= inv.getName() %></strong></td>
                                                    <td><%= inv.getDepartment() %></td>
                                                    <td>
                                                        <a href="<%=request.getContextPath()%>/admin/invigilators?action=delete&id=<%= inv.getInvigilatorId() %>" 
                                                           class="btn-icon delete" onclick="return confirm('Delete this invigilator?')">
                                                            <i class="bi bi-trash3"></i>
                                                        </a>
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            <% } %>
                        </div>
                    </div>
                    
                    <!-- Duty Schedule View -->
                    <div class="glass-card mt-4">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom"><i class="bi bi-calendar-check"></i> Duty Schedule</h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/invigilators" method="get" class="d-flex gap-3 mb-3">
                                <select class="form-control-custom" name="examId" onchange="this.form.submit()">
                                    <option value="">-- View duties for exam --</option>
                                    <% if (exams != null) { for (ExamSession e : exams) { %>
                                        <option value="<%= e.getExamId() %>"
                                                <%= (selectedExamId != null && selectedExamId == e.getExamId()) ? "selected" : "" %>>
                                            <%= e.getExamName() %>
                                        </option>
                                    <% } } %>
                                </select>
                            </form>
                            
                            <% if (duties != null && !duties.isEmpty()) { %>
                                <div class="table-responsive">
                                    <table class="table-custom">
                                        <thead>
                                            <tr><th>Room</th><th>Invigilator</th><th>Department</th><th>Duty Slot</th></tr>
                                        </thead>
                                        <tbody>
                                            <% for (DutySchedule d : duties) { %>
                                                <tr>
                                                    <td><span class="room-badge"><%= d.getRoomNo() %></span></td>
                                                    <td><strong><%= d.getInvigilatorName() %></strong></td>
                                                    <td><%= d.getDepartment() %></td>
                                                    <td><%= d.getDutySlot() %></td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            <% } else if (selectedExamId != null) { %>
                                <div class="empty-state small">
                                    <p>No duties assigned for this exam yet. Use the Auto-Assign button.</p>
                                </div>
                            <% } else { %>
                                <div class="empty-state small">
                                    <p>Select an exam above to view duty assignments.</p>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
