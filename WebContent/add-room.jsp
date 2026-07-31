<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.Room" %>
<%@ page import="java.util.List" %>
<%
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    Room editRoom = (Room) request.getAttribute("editRoom");
    
    // Flash messages
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
    <title>Manage Rooms - Exam Seating Allocator</title>
    <meta name="description" content="Add, edit, and manage exam rooms for seating allocation">
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
                <h1 class="page-title"><i class="bi bi-building"></i> Manage Rooms</h1>
                <p class="page-subtitle">Add and configure exam halls with seating grid layouts</p>
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
                <!-- Add/Edit Room Form -->
                <div class="col-lg-5">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-plus-circle"></i>
                                <%= editRoom != null ? "Edit Room" : "Add New Room" %>
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/rooms" method="post" id="roomForm" onsubmit="return validateRoomForm()">
                                <% if (editRoom != null) { %>
                                    <input type="hidden" name="roomId" value="<%= editRoom.getRoomId() %>">
                                <% } %>
                                
                                <div class="form-group-custom">
                                    <label for="roomNo" class="form-label-custom">Room Number</label>
                                    <input type="text" class="form-control-custom" id="roomNo" name="roomNo" 
                                           value="<%= editRoom != null ? editRoom.getRoomNo() : "" %>" 
                                           placeholder="e.g., A101" required>
                                </div>
                                
                                <div class="row g-3">
                                    <div class="col-6">
                                        <div class="form-group-custom">
                                            <label for="rowsCount" class="form-label-custom">Rows</label>
                                            <input type="number" class="form-control-custom" id="rowsCount" name="rowsCount"
                                                   value="<%= editRoom != null ? editRoom.getRowsCount() : "" %>"
                                                   placeholder="e.g., 5" min="1" max="20" required>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="form-group-custom">
                                            <label for="colsCount" class="form-label-custom">Columns</label>
                                            <input type="number" class="form-control-custom" id="colsCount" name="colsCount"
                                                   value="<%= editRoom != null ? editRoom.getColsCount() : "" %>"
                                                   placeholder="e.g., 6" min="1" max="20" required>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="capacity-preview" id="capacityPreview">
                                    <i class="bi bi-grid-3x3-gap"></i>
                                    <span>Capacity: <strong id="capacityValue">—</strong> seats</span>
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-save"></i>
                                        <%= editRoom != null ? "Update Room" : "Add Room" %>
                                    </button>
                                    <% if (editRoom != null) { %>
                                        <a href="<%=request.getContextPath()%>/admin/rooms" class="btn-secondary-custom">
                                            <i class="bi bi-x-circle"></i> Cancel
                                        </a>
                                    <% } %>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <!-- Room List -->
                <div class="col-lg-7">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-list-ul"></i> Existing Rooms
                                <span class="badge-count"><%= rooms != null ? rooms.size() : 0 %></span>
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <% if (rooms == null || rooms.isEmpty()) { %>
                                <div class="empty-state">
                                    <i class="bi bi-building empty-icon"></i>
                                    <h3>No rooms added yet</h3>
                                    <p>Add your first exam room using the form on the left.</p>
                                </div>
                            <% } else { %>
                                <div class="table-responsive">
                                    <table class="table-custom">
                                        <thead>
                                            <tr>
                                                <th>Room No</th>
                                                <th>Grid</th>
                                                <th>Capacity</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% for (Room r : rooms) { %>
                                                <tr>
                                                    <td><span class="room-badge"><%= r.getRoomNo() %></span></td>
                                                    <td><%= r.getRowsCount() %> × <%= r.getColsCount() %></td>
                                                    <td><span class="capacity-badge"><%= r.getCapacity() %> seats</span></td>
                                                    <td>
                                                        <div class="action-btns">
                                                            <a href="<%=request.getContextPath()%>/admin/rooms?action=edit&id=<%= r.getRoomId() %>" 
                                                               class="btn-icon edit" title="Edit">
                                                                <i class="bi bi-pencil-square"></i>
                                                            </a>
                                                            <a href="<%=request.getContextPath()%>/admin/rooms?action=delete&id=<%= r.getRoomId() %>" 
                                                               class="btn-icon delete" title="Delete"
                                                               onclick="return confirm('Delete room <%= r.getRoomNo() %>?')">
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
        // Auto-calculate capacity preview
        const rowsInput = document.getElementById('rowsCount');
        const colsInput = document.getElementById('colsCount');
        const capacityVal = document.getElementById('capacityValue');
        
        function updateCapacity() {
            const rows = parseInt(rowsInput.value) || 0;
            const cols = parseInt(colsInput.value) || 0;
            capacityVal.textContent = rows * cols > 0 ? rows * cols : '—';
        }
        
        rowsInput.addEventListener('input', updateCapacity);
        colsInput.addEventListener('input', updateCapacity);
        updateCapacity(); // Initialize on page load
        
        // Client-side validation
        function validateRoomForm() {
            const roomNo = document.getElementById('roomNo').value.trim();
            const rows = parseInt(rowsInput.value);
            const cols = parseInt(colsInput.value);
            
            if (!roomNo) { alert('Room number is required.'); return false; }
            if (isNaN(rows) || rows < 1 || rows > 20) { alert('Rows must be between 1 and 20.'); return false; }
            if (isNaN(cols) || cols < 1 || cols > 20) { alert('Columns must be between 1 and 20.'); return false; }
            return true;
        }
    </script>
</body>
</html>
