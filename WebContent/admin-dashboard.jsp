<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Integer totalExams = (Integer) request.getAttribute("totalExams");
    Integer totalStudents = (Integer) request.getAttribute("totalStudents");
    Integer totalRooms = (Integer) request.getAttribute("totalRooms");
    Integer totalInvigilators = (Integer) request.getAttribute("totalInvigilators");
    
    if (totalExams == null) totalExams = 0;
    if (totalStudents == null) totalStudents = 0;
    if (totalRooms == null) totalRooms = 0;
    if (totalInvigilators == null) totalInvigilators = 0;
    
    String adminUser = (String) session.getAttribute("adminUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Exam Seating Allocator</title>
    <meta name="description" content="Admin dashboard showing system overview and quick actions">
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
                <h1 class="page-title">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </h1>
                <p class="page-subtitle">
                    Welcome back, <strong><%= adminUser != null ? adminUser : "Admin" %></strong>! 
                    Here's your system overview.
                </p>
            </div>
            
            <!-- Stats Cards -->
            <div class="row g-4 mb-4">
                <div class="col-sm-6 col-lg-3">
                    <div class="stat-card" style="--stat-color: #6366f1">
                        <div class="stat-icon">
                            <i class="bi bi-calendar-event"></i>
                        </div>
                        <div class="stat-info">
                            <span class="stat-value"><%= totalExams %></span>
                            <span class="stat-label">Exam Sessions</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="stat-card" style="--stat-color: #10b981">
                        <div class="stat-icon">
                            <i class="bi bi-people"></i>
                        </div>
                        <div class="stat-info">
                            <span class="stat-value"><%= totalStudents %></span>
                            <span class="stat-label">Students</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="stat-card" style="--stat-color: #06b6d4">
                        <div class="stat-icon">
                            <i class="bi bi-building"></i>
                        </div>
                        <div class="stat-info">
                            <span class="stat-value"><%= totalRooms %></span>
                            <span class="stat-label">Rooms</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="stat-card" style="--stat-color: #a855f7">
                        <div class="stat-icon">
                            <i class="bi bi-person-badge"></i>
                        </div>
                        <div class="stat-info">
                            <span class="stat-value"><%= totalInvigilators %></span>
                            <span class="stat-label">Invigilators</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Quick Actions -->
            <div class="glass-card">
                <div class="card-header-custom">
                    <h2 class="card-title-custom"><i class="bi bi-lightning"></i> Quick Actions</h2>
                </div>
                <div class="card-body-custom">
                    <div class="row g-3">
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/rooms" class="quick-action-btn">
                                <i class="bi bi-building"></i>
                                <span>Manage Rooms</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/exams" class="quick-action-btn">
                                <i class="bi bi-calendar-plus"></i>
                                <span>Create Exam</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/students" class="quick-action-btn">
                                <i class="bi bi-cloud-upload"></i>
                                <span>Upload Students</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/allocate" class="quick-action-btn highlight">
                                <i class="bi bi-cpu"></i>
                                <span>Run Allocation</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/seating-chart" class="quick-action-btn">
                                <i class="bi bi-grid-3x3-gap"></i>
                                <span>View Chart</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/admin/invigilators" class="quick-action-btn">
                                <i class="bi bi-person-badge"></i>
                                <span>Invigilators</span>
                            </a>
                        </div>
                        <div class="col-sm-6 col-lg-3">
                            <a href="<%=request.getContextPath()%>/student-lookup" class="quick-action-btn" target="_blank">
                                <i class="bi bi-search"></i>
                                <span>Student Lookup</span>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Workflow Guide -->
            <div class="glass-card mt-4">
                <div class="card-header-custom">
                    <h2 class="card-title-custom"><i class="bi bi-signpost-2"></i> Workflow Guide</h2>
                </div>
                <div class="card-body-custom">
                    <div class="workflow-steps">
                        <div class="workflow-step">
                            <div class="step-number">1</div>
                            <div class="step-content">
                                <h4>Add Rooms</h4>
                                <p>Configure exam halls with their seating grid dimensions</p>
                            </div>
                        </div>
                        <div class="workflow-step">
                            <div class="step-number">2</div>
                            <div class="step-content">
                                <h4>Create Exam</h4>
                                <p>Set up an exam session with date and time slot</p>
                            </div>
                        </div>
                        <div class="workflow-step">
                            <div class="step-number">3</div>
                            <div class="step-content">
                                <h4>Upload Students</h4>
                                <p>Add students individually or via CSV bulk upload</p>
                            </div>
                        </div>
                        <div class="workflow-step">
                            <div class="step-number">4</div>
                            <div class="step-content">
                                <h4>Run Allocation</h4>
                                <p>Execute the anti-cheating algorithm to assign seats</p>
                            </div>
                        </div>
                        <div class="workflow-step">
                            <div class="step-number">5</div>
                            <div class="step-content">
                                <h4>View & Print</h4>
                                <p>View seating charts and print them for each room</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
