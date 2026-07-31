<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Determine active page for sidebar highlighting
    String currentURI = request.getRequestURI();
    String ctx = request.getContextPath();
%>
<!-- Sidebar Navigation -->
<nav class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="logo-container">
            <div class="logo-icon">
                <i class="bi bi-shield-check"></i>
            </div>
            <div class="logo-text">
                <h2>ExamSeat</h2>
                <span>Anti-Cheat Allocator</span>
            </div>
        </div>
    </div>
    
    <div class="sidebar-nav">
        <div class="nav-section">
            <span class="nav-section-title">Main</span>
            <a href="<%=ctx%>/admin/dashboard" class="nav-link-custom <%= currentURI.contains("dashboard") ? "active" : "" %>">
                <i class="bi bi-speedometer2"></i>
                <span>Dashboard</span>
            </a>
        </div>
        
        <div class="nav-section">
            <span class="nav-section-title">Management</span>
            <a href="<%=ctx%>/admin/rooms" class="nav-link-custom <%= currentURI.contains("room") ? "active" : "" %>">
                <i class="bi bi-building"></i>
                <span>Rooms</span>
            </a>
            <a href="<%=ctx%>/admin/exams" class="nav-link-custom <%= currentURI.contains("exam") ? "active" : "" %>">
                <i class="bi bi-calendar-event"></i>
                <span>Exams</span>
            </a>
            <a href="<%=ctx%>/admin/students" class="nav-link-custom <%= currentURI.contains("student") && !currentURI.contains("lookup") ? "active" : "" %>">
                <i class="bi bi-people"></i>
                <span>Students</span>
            </a>
            <a href="<%=ctx%>/admin/invigilators" class="nav-link-custom <%= currentURI.contains("invigilator") ? "active" : "" %>">
                <i class="bi bi-person-badge"></i>
                <span>Invigilators</span>
            </a>
        </div>
        
        <div class="nav-section">
            <span class="nav-section-title">Allocation</span>
            <a href="<%=ctx%>/admin/allocate" class="nav-link-custom <%= currentURI.contains("allocate") ? "active" : "" %>">
                <i class="bi bi-cpu"></i>
                <span>Run Allocation</span>
            </a>
            <a href="<%=ctx%>/admin/seating-chart" class="nav-link-custom <%= currentURI.contains("seating-chart") ? "active" : "" %>">
                <i class="bi bi-grid-3x3-gap"></i>
                <span>Seating Chart</span>
            </a>
        </div>
        
        <div class="nav-section">
            <span class="nav-section-title">Public</span>
            <a href="<%=ctx%>/student-lookup" class="nav-link-custom <%= currentURI.contains("lookup") ? "active" : "" %>">
                <i class="bi bi-search"></i>
                <span>Student Lookup</span>
            </a>
        </div>
    </div>
    
    <div class="sidebar-footer">
        <a href="<%=ctx%>/logout" class="nav-link-custom logout-link">
            <i class="bi bi-box-arrow-left"></i>
            <span>Logout</span>
        </a>
    </div>
</nav>

<!-- Mobile sidebar toggle -->
<button class="sidebar-toggle d-lg-none" id="sidebarToggle" onclick="document.getElementById('sidebar').classList.toggle('show')">
    <i class="bi bi-list"></i>
</button>
