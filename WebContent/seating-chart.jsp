<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.*" %>
<%@ page import="java.util.*" %>
<%
    List<ExamSession> exams = (List<ExamSession>) request.getAttribute("exams");
    ExamSession selectedExam = (ExamSession) request.getAttribute("selectedExam");
    Map<Integer, List<SeatAllocation>> roomAllocations = (Map<Integer, List<SeatAllocation>>) request.getAttribute("roomAllocations");
    Map<Integer, String> roomNames = (Map<Integer, String>) request.getAttribute("roomNames");
    Map<Integer, Room> roomMap = (Map<Integer, Room>) request.getAttribute("roomMap");
    Set<String> subjects = (Set<String>) request.getAttribute("subjects");
    Integer totalAllocations = (Integer) request.getAttribute("totalAllocations");
    
    String successMsg = (String) request.getAttribute("successMsg");
    
    // Vibrant subject color palette for anti-cheating seat distinction
    String[] subjectColors = {
        "#6366f1", "#06b6d4", "#ec4899", "#10b981", "#f59e0b", 
        "#a855f7", "#f43f5e", "#3b82f6", "#84cc16", "#14b8a6"
    };
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seating Chart - Exam Seating Allocator</title>
    <meta name="description" content="Visual seating chart showing student seat assignments">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Outfit:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="app-layout">
        <%@ include file="sidebar.jsp" %>
        
        <main class="main-content">
            <div class="page-header no-print">
                <h1 class="page-title"><i class="bi bi-grid-3x3-gap"></i> Seating Chart</h1>
                <p class="page-subtitle">Visual room-wise seating arrangement</p>
            </div>
            
            <% if (successMsg != null) { %>
                <div class="alert alert-success alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i><%= successMsg %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>
            
            <!-- Exam Selector -->
            <div class="glass-card no-print">
                <div class="card-body-custom">
                    <form action="<%=request.getContextPath()%>/admin/seating-chart" method="get" class="d-flex gap-3 align-items-end">
                        <div class="form-group-custom flex-grow-1 mb-0">
                            <label for="examId" class="form-label-custom">Select Exam</label>
                            <select class="form-control-custom" name="examId" id="examId" onchange="this.form.submit()">
                                <option value="">-- Choose an exam --</option>
                                <% if (exams != null) { for (ExamSession e : exams) { %>
                                    <option value="<%= e.getExamId() %>" 
                                            <%= (selectedExam != null && selectedExam.getExamId() == e.getExamId()) ? "selected" : "" %>>
                                        <%= e.getExamName() %> (<%= e.getExamDate() %>)
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                        <% if (selectedExam != null && roomAllocations != null) { %>
                            <button type="button" class="btn-primary-custom" onclick="window.print()">
                                <i class="bi bi-printer"></i> Print Chart
                            </button>
                        <% } %>
                    </form>
                </div>
            </div>
            
            <% if (selectedExam == null) { %>
                <div class="glass-card mt-4">
                    <div class="card-body-custom">
                        <div class="empty-state">
                            <i class="bi bi-grid-3x3 empty-icon"></i>
                            <h3>Select an exam to view the seating chart</h3>
                            <p>Choose an exam session from the dropdown above.</p>
                        </div>
                    </div>
                </div>
            <% } else if (roomAllocations == null || roomAllocations.isEmpty()) { %>
                <div class="glass-card mt-4">
                    <div class="card-body-custom">
                        <div class="empty-state">
                            <i class="bi bi-exclamation-diamond empty-icon"></i>
                            <h3>No allocation found for this exam</h3>
                            <p>Run the allocation algorithm first from the
                                <a href="<%=request.getContextPath()%>/admin/allocate">Allocate Seats</a> page.</p>
                        </div>
                    </div>
                </div>
            <% } else { %>
            
                <!-- Print Header (only visible when printing) -->
                <div class="print-header">
                    <h1>Seating Chart — <%= selectedExam.getExamName() %></h1>
                    <p>Date: <%= selectedExam.getExamDate() %> | Time: <%= selectedExam.getExamTime() %> | 
                       Total Students: <%= totalAllocations %></p>
                </div>
                
                <!-- Subject Legend -->
                <div class="glass-card mt-4 subject-legend-card">
                    <div class="card-body-custom">
                        <div class="subject-legend">
                            <strong class="me-3"><i class="bi bi-palette"></i> Subject Legend:</strong>
                            <% if (subjects != null) { 
                                int colorIdx = 0;
                                for (String subj : subjects) { %>
                                <span class="legend-item" style="--legend-color: <%= subjectColors[colorIdx % subjectColors.length] %>">
                                    <span class="legend-dot"></span> <%= subj %>
                                </span>
                            <%  colorIdx++; } } %>
                            <span class="ms-auto"><strong><%= totalAllocations %></strong> students seated</span>
                        </div>
                    </div>
                </div>
                
                <!-- Room-wise Seating Grids -->
                <% 
                    int colorIndex = 0;
                    Map<String, String> subjectColorMap = new LinkedHashMap<>();
                    if (subjects != null) {
                        for (String subj : subjects) {
                            subjectColorMap.put(subj, subjectColors[colorIndex % subjectColors.length]);
                            colorIndex++;
                        }
                    }
                    
                    for (Map.Entry<Integer, List<SeatAllocation>> entry : roomAllocations.entrySet()) {
                        int roomId = entry.getKey();
                        List<SeatAllocation> allocs = entry.getValue();
                        Room room = roomMap.get(roomId);
                        String roomNo = roomNames.get(roomId);
                        
                        if (room == null) continue;
                        
                        // Build 2D grid
                        SeatAllocation[][] grid = new SeatAllocation[room.getRowsCount()][room.getColsCount()];
                        for (SeatAllocation a : allocs) {
                            if (a.getSeatRow() < room.getRowsCount() && a.getSeatCol() < room.getColsCount()) {
                                grid[a.getSeatRow()][a.getSeatCol()] = a;
                            }
                        }
                %>
                    <div class="glass-card mt-4 room-chart-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-building"></i> Room: <%= roomNo %>
                                <span class="badge-count"><%= allocs.size() %> / <%= room.getCapacity() %> seats</span>
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <div class="seating-grid-container">
                                <!-- Blackboard/Front indicator -->
                                <div class="front-indicator">
                                    <span>▼ FRONT (Blackboard) ▼</span>
                                </div>
                                
                                <table class="seating-grid">
                                    <thead>
                                        <tr>
                                            <th class="row-label"></th>
                                            <% for (int c = 0; c < room.getColsCount(); c++) { %>
                                                <th class="col-label">C<%= c+1 %></th>
                                            <% } %>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (int r = 0; r < room.getRowsCount(); r++) { %>
                                            <tr>
                                                <td class="row-label">R<%= r+1 %></td>
                                                <% for (int c = 0; c < room.getColsCount(); c++) {
                                                    SeatAllocation seat = grid[r][c];
                                                    if (seat != null) {
                                                        String sColor = subjectColorMap.getOrDefault(seat.getSubjectCode(), "#888");
                                                %>
                                                    <td class="seat occupied" style="--seat-color: <%= sColor %>"
                                                        title="<%= seat.getStudentName() %> | <%= seat.getSubjectCode() %> | <%= seat.getBranch() %> <%= seat.getClassYear() %>">
                                                        <div class="seat-roll"><%= seat.getRollNo() %></div>
                                                        <div class="seat-subject"><%= seat.getSubjectCode() %></div>
                                                    </td>
                                                <% } else { %>
                                                    <td class="seat empty-seat">
                                                        <span class="empty-marker">—</span>
                                                    </td>
                                                <% } } %>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                <% } %>
            <% } %>
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
