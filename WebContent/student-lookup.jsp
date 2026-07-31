<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.*" %>
<%@ page import="java.util.*" %>
<%
    List<ExamSession> exams = (List<ExamSession>) request.getAttribute("exams");
    SeatAllocation result = (SeatAllocation) request.getAttribute("result");
    ExamSession resultExam = (ExamSession) request.getAttribute("resultExam");
    Room resultRoom = (Room) request.getAttribute("resultRoom");
    String errorMsg = (String) request.getAttribute("errorMsg");
    String searchRollNo = (String) request.getAttribute("searchRollNo");
    String searchExamId = (String) request.getAttribute("searchExamId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Your Seat - Exam Seating Allocator</title>
    <meta name="description" content="Look up your exam seat assignment by roll number">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Outfit:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/css/style.css" rel="stylesheet">
</head>
<body class="public-page">
    <div class="public-container">
        <!-- Header -->
        <div class="public-header">
            <div class="logo-container">
                <div class="logo-icon large">
                    <i class="bi bi-shield-check"></i>
                </div>
                <h1>Exam Seat Finder</h1>
                <p>Enter your roll number to find your assigned seat</p>
            </div>
        </div>
        
        <!-- Search Form -->
        <div class="glass-card lookup-card">
            <div class="card-body-custom">
                <form action="<%=request.getContextPath()%>/student-lookup" method="post" id="lookupForm">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-5">
                            <label for="rollNo" class="form-label-custom">Roll Number</label>
                            <input type="text" class="form-control-custom" id="rollNo" name="rollNo" 
                                   value="<%= searchRollNo != null ? searchRollNo : "" %>"
                                   placeholder="e.g., CS2024001" required>
                        </div>
                        <div class="col-md-5">
                            <label for="examId" class="form-label-custom">Exam</label>
                            <select class="form-control-custom" id="examId" name="examId" required>
                                <option value="">-- Select Exam --</option>
                                <% if (exams != null) { for (ExamSession e : exams) { %>
                                    <option value="<%= e.getExamId() %>"
                                            <%= (searchExamId != null && searchExamId.equals(String.valueOf(e.getExamId()))) ? "selected" : "" %>>
                                        <%= e.getExamName() %> (<%= e.getExamDate() %>)
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn-primary-custom w-100">
                                <i class="bi bi-search"></i> Find
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <% if (errorMsg != null) { %>
            <div class="alert alert-danger mt-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMsg %>
            </div>
        <% } %>
        
        <% if (result != null && resultExam != null && resultRoom != null) { %>
            <!-- Result Card -->
            <div class="glass-card mt-4 result-card">
                <div class="card-body-custom">
                    <div class="result-header">
                        <i class="bi bi-check-circle-fill result-icon"></i>
                        <h2>Seat Found!</h2>
                    </div>
                    
                    <div class="result-details">
                        <div class="result-row">
                            <div class="result-item">
                                <span class="result-label">Student</span>
                                <span class="result-value"><%= result.getStudentName() %></span>
                            </div>
                            <div class="result-item">
                                <span class="result-label">Roll No</span>
                                <span class="result-value highlight"><%= result.getRollNo() %></span>
                            </div>
                            <div class="result-item">
                                <span class="result-label">Subject</span>
                                <span class="result-value"><%= result.getSubjectCode() %></span>
                            </div>
                        </div>
                        
                        <div class="result-divider"></div>
                        
                        <div class="result-row">
                            <div class="result-item large">
                                <span class="result-label">Room</span>
                                <span class="result-value room-highlight"><%= result.getRoomNo() %></span>
                            </div>
                            <div class="result-item large">
                                <span class="result-label">Seat Position</span>
                                <span class="result-value seat-highlight">
                                    Row <%= result.getSeatRow() + 1 %>, Column <%= result.getSeatCol() + 1 %>
                                </span>
                            </div>
                        </div>
                        
                        <div class="result-divider"></div>
                        
                        <div class="result-row">
                            <div class="result-item">
                                <span class="result-label">Exam</span>
                                <span class="result-value"><%= resultExam.getExamName() %></span>
                            </div>
                            <div class="result-item">
                                <span class="result-label">Date</span>
                                <span class="result-value"><%= resultExam.getExamDate() %></span>
                            </div>
                            <div class="result-item">
                                <span class="result-label">Time</span>
                                <span class="result-value"><%= resultExam.getExamTime() %></span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Mini Room Grid Preview -->
                    <div class="mini-grid-container mt-4">
                        <h3 class="mini-grid-title">
                            <i class="bi bi-geo-alt"></i> Your seat in Room <%= result.getRoomNo() %>
                        </h3>
                        <div class="mini-grid">
                            <div class="mini-front">FRONT</div>
                            <table class="mini-grid-table">
                                <% for (int r = 0; r < resultRoom.getRowsCount(); r++) { %>
                                    <tr>
                                        <% for (int c = 0; c < resultRoom.getColsCount(); c++) { 
                                            boolean isMySpot = (r == result.getSeatRow() && c == result.getSeatCol());
                                        %>
                                            <td class="mini-seat <%= isMySpot ? "my-seat" : "" %>">
                                                <% if (isMySpot) { %>
                                                    <i class="bi bi-geo-alt-fill"></i>
                                                <% } %>
                                            </td>
                                        <% } %>
                                    </tr>
                                <% } %>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        <% } %>
        
        <!-- Footer link to admin -->
        <div class="public-footer mt-4">
            <a href="<%=request.getContextPath()%>/login" class="admin-link">
                <i class="bi bi-lock"></i> Admin Login
            </a>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
