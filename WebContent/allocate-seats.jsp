<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.ExamSession" %>
<%@ page import="com.examseating.model.Room" %>
<%@ page import="java.util.List" %>
<%
    List<ExamSession> exams = (List<ExamSession>) request.getAttribute("exams");
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    Integer studentCount = (Integer) request.getAttribute("studentCount");
    
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
    <title>Allocate Seats - Exam Seating Allocator</title>
    <meta name="description" content="Run the anti-cheating seating allocation algorithm">
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
                <h1 class="page-title"><i class="bi bi-cpu"></i> Run Seat Allocation</h1>
                <p class="page-subtitle">Execute the anti-cheating algorithm to assign students to seats</p>
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
            
            <% if (exams == null || exams.isEmpty() || rooms == null || rooms.isEmpty() || studentCount == null || studentCount == 0) { %>
                <div class="glass-card">
                    <div class="card-body-custom">
                        <div class="empty-state">
                            <i class="bi bi-exclamation-diamond empty-icon"></i>
                            <h3>Prerequisites Missing</h3>
                            <p>Before running allocation, make sure you have:</p>
                            <ul class="prereq-list">
                                <li class="<%= (exams != null && !exams.isEmpty()) ? "done" : "" %>">
                                    <i class="bi bi-<%= (exams != null && !exams.isEmpty()) ? "check-circle-fill" : "circle" %>"></i>
                                    At least one exam session created
                                </li>
                                <li class="<%= (rooms != null && !rooms.isEmpty()) ? "done" : "" %>">
                                    <i class="bi bi-<%= (rooms != null && !rooms.isEmpty()) ? "check-circle-fill" : "circle" %>"></i>
                                    At least one room configured
                                </li>
                                <li class="<%= (studentCount != null && studentCount > 0) ? "done" : "" %>">
                                    <i class="bi bi-<%= (studentCount != null && studentCount > 0) ? "check-circle-fill" : "circle" %>"></i>
                                    Students uploaded to the system
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            <% } else { %>
                <form action="<%=request.getContextPath()%>/admin/allocate" method="post" id="allocateForm">
                    <div class="row g-4">
                        <!-- Exam Selection -->
                        <div class="col-lg-6">
                            <div class="glass-card">
                                <div class="card-header-custom">
                                    <h2 class="card-title-custom">
                                        <i class="bi bi-calendar-check"></i> Select Exam
                                    </h2>
                                </div>
                                <div class="card-body-custom">
                                    <div class="exam-select-list">
                                        <% for (ExamSession e : exams) { 
                                            boolean hasAlloc = request.getAttribute("hasAllocation_" + e.getExamId()) != null;
                                        %>
                                            <label class="exam-select-item">
                                                <input type="radio" name="examId" value="<%= e.getExamId() %>" required>
                                                <div class="exam-select-content">
                                                    <strong><%= e.getExamName() %></strong>
                                                    <span class="date-badge"><%= e.getExamDate() %></span>
                                                    <span><%= e.getExamTime() %></span>
                                                    <% if (hasAlloc) { %>
                                                        <span class="status-badge warning">
                                                            <i class="bi bi-arrow-repeat"></i> Already allocated (will re-run)
                                                        </span>
                                                    <% } %>
                                                </div>
                                            </label>
                                        <% } %>
                                    </div>
                                    
                                    <div class="info-box mt-3">
                                        <i class="bi bi-people-fill"></i>
                                        <strong><%= studentCount %></strong> students will be allocated
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Room Selection -->
                        <div class="col-lg-6">
                            <div class="glass-card">
                                <div class="card-header-custom">
                                    <h2 class="card-title-custom">
                                        <i class="bi bi-building"></i> Select Rooms
                                    </h2>
                                </div>
                                <div class="card-body-custom">
                                    <div class="room-select-list">
                                        <% int totalCap = 0; for (Room r : rooms) { totalCap += r.getCapacity(); %>
                                            <label class="room-select-item">
                                                <input type="checkbox" name="roomIds" value="<%= r.getRoomId() %>">
                                                <div class="room-select-content">
                                                    <span class="room-badge"><%= r.getRoomNo() %></span>
                                                    <span><%= r.getRowsCount() %> × <%= r.getColsCount() %></span>
                                                    <span class="capacity-badge"><%= r.getCapacity() %> seats</span>
                                                </div>
                                            </label>
                                        <% } %>
                                    </div>
                                    
                                    <div class="d-flex justify-content-between mt-3">
                                        <button type="button" class="btn-secondary-custom" onclick="selectAllRooms()">
                                            <i class="bi bi-check-all"></i> Select All
                                        </button>
                                        <div class="info-box">
                                            Total capacity: <strong id="selectedCapacity">0</strong> / <%= totalCap %> seats
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Run Button -->
                    <div class="text-center mt-4">
                        <button type="submit" class="btn-primary-custom btn-lg" id="runBtn" 
                                onclick="return confirmAllocation()">
                            <i class="bi bi-play-circle"></i> Run Allocation Algorithm
                        </button>
                    </div>
                </form>
            <% } %>
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function selectAllRooms() {
            document.querySelectorAll('input[name="roomIds"]').forEach(cb => cb.checked = true);
            updateCapacity();
        }
        
        function updateCapacity() {
            let total = 0;
            document.querySelectorAll('input[name="roomIds"]:checked').forEach(cb => {
                const capText = cb.closest('.room-select-item').querySelector('.capacity-badge').textContent;
                total += parseInt(capText);
            });
            document.getElementById('selectedCapacity').textContent = total;
        }
        
        document.querySelectorAll('input[name="roomIds"]').forEach(cb => {
            cb.addEventListener('change', updateCapacity);
        });
        
        function confirmAllocation() {
            const exam = document.querySelector('input[name="examId"]:checked');
            const rooms = document.querySelectorAll('input[name="roomIds"]:checked');
            
            if (!exam) { alert('Please select an exam session.'); return false; }
            if (rooms.length === 0) { alert('Please select at least one room.'); return false; }
            
            return confirm('Run the seating allocation? This will replace any existing allocation for the selected exam.');
        }
    </script>
</body>
</html>
