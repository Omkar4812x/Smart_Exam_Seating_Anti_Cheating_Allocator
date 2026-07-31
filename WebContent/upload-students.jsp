<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.examseating.model.Student" %>
<%@ page import="java.util.List" %>
<%
    List<Student> students = (List<Student>) request.getAttribute("students");
    
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
    <title>Upload Students - Exam Seating Allocator</title>
    <meta name="description" content="Upload students individually or via CSV for exam seating allocation">
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
                <h1 class="page-title"><i class="bi bi-people"></i> Manage Students</h1>
                <p class="page-subtitle">Add students individually or upload via CSV file</p>
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
                <!-- Single Student Form -->
                <div class="col-lg-6">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-person-plus"></i> Add Single Student
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/students" method="post" id="singleForm" onsubmit="return validateSingleForm()">
                                <input type="hidden" name="uploadType" value="single">
                                
                                <div class="form-group-custom">
                                    <label for="rollNo" class="form-label-custom">Roll Number *</label>
                                    <input type="text" class="form-control-custom" id="rollNo" name="rollNo" 
                                           placeholder="e.g., CS2024001" required>
                                </div>
                                
                                <div class="form-group-custom">
                                    <label for="studentName" class="form-label-custom">Full Name</label>
                                    <input type="text" class="form-control-custom" id="studentName" name="studentName" 
                                           placeholder="e.g., Aarav Sharma">
                                </div>
                                
                                <div class="row g-3">
                                    <div class="col-6">
                                        <div class="form-group-custom">
                                            <label for="branch" class="form-label-custom">Branch</label>
                                            <input type="text" class="form-control-custom" id="branch" name="branch" 
                                                   placeholder="e.g., CSE">
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="form-group-custom">
                                            <label for="classYear" class="form-label-custom">Class Year</label>
                                            <input type="text" class="form-control-custom" id="classYear" name="classYear" 
                                                   placeholder="e.g., 2nd Year">
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group-custom">
                                    <label for="subjectCode" class="form-label-custom">Subject Code *</label>
                                    <input type="text" class="form-control-custom" id="subjectCode" name="subjectCode" 
                                           placeholder="e.g., CS301" required>
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-person-plus"></i> Add Student
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                <!-- CSV Upload -->
                <div class="col-lg-6">
                    <div class="glass-card">
                        <div class="card-header-custom">
                            <h2 class="card-title-custom">
                                <i class="bi bi-file-earmark-spreadsheet"></i> CSV Bulk Upload
                            </h2>
                        </div>
                        <div class="card-body-custom">
                            <form action="<%=request.getContextPath()%>/admin/students" method="post" 
                                  enctype="multipart/form-data" id="csvForm">
                                <input type="hidden" name="uploadType" value="csv">
                                
                                <div class="csv-upload-zone" id="dropZone">
                                    <i class="bi bi-cloud-arrow-up csv-upload-icon"></i>
                                    <h3>Drag & Drop CSV File</h3>
                                    <p>or click to browse</p>
                                    <input type="file" class="csv-file-input" id="csvFile" name="csvFile" 
                                           accept=".csv" onchange="updateFileName(this)">
                                    <span class="csv-file-name" id="fileName"></span>
                                </div>
                                
                                <div class="csv-format-info">
                                    <h4><i class="bi bi-info-circle"></i> CSV Format</h4>
                                    <code>roll_no, name, branch, class_year, subject_code</code>
                                    <p class="mt-2 mb-0">Header row is auto-detected and skipped. Each row = one student.</p>
                                </div>
                                
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary-custom">
                                        <i class="bi bi-upload"></i> Upload CSV
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Student List -->
            <div class="glass-card mt-4">
                <div class="card-header-custom d-flex justify-content-between align-items-center">
                    <h2 class="card-title-custom">
                        <i class="bi bi-list-ul"></i> Student Records
                        <span class="badge-count"><%= students != null ? students.size() : 0 %></span>
                    </h2>
                    <div class="d-flex gap-2 align-items-center">
                        <input type="text" class="form-control-custom search-input" id="studentSearch" 
                               placeholder="Search by roll no, name, or subject..." onkeyup="filterStudents()">
                        <% if (students != null && !students.isEmpty()) { %>
                            <a href="<%=request.getContextPath()%>/admin/students?action=deleteAll" 
                               class="btn-danger-custom" onclick="return confirm('Delete ALL students? This cannot be undone.')">
                                <i class="bi bi-trash3"></i> Clear All
                            </a>
                        <% } %>
                    </div>
                </div>
                <div class="card-body-custom">
                    <% if (students == null || students.isEmpty()) { %>
                        <div class="empty-state">
                            <i class="bi bi-people empty-icon"></i>
                            <h3>No students uploaded yet</h3>
                            <p>Add students using the form above or upload a CSV file.</p>
                        </div>
                    <% } else { %>
                        <div class="table-responsive">
                            <table class="table-custom" id="studentTable">
                                <thead>
                                    <tr>
                                        <th>Roll No</th>
                                        <th>Name</th>
                                        <th>Branch</th>
                                        <th>Class Year</th>
                                        <th>Subject</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Student s : students) { %>
                                        <tr>
                                            <td><span class="roll-badge"><%= s.getRollNo() %></span></td>
                                            <td><%= s.getName() %></td>
                                            <td><%= s.getBranch() %></td>
                                            <td><%= s.getClassYear() %></td>
                                            <td><span class="subject-badge"><%= s.getSubjectCode() %></span></td>
                                            <td>
                                                <a href="<%=request.getContextPath()%>/admin/students?action=delete&id=<%= s.getStudentId() %>" 
                                                   class="btn-icon delete" title="Delete"
                                                   onclick="return confirm('Delete student <%= s.getRollNo() %>?')">
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
        </main>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Client-side validation
        function validateSingleForm() {
            const rollNo = document.getElementById('rollNo').value.trim();
            const subject = document.getElementById('subjectCode').value.trim();
            if (!rollNo) { alert('Roll number is required.'); return false; }
            if (!subject) { alert('Subject code is required.'); return false; }
            return true;
        }
        
        // File name display for CSV upload
        function updateFileName(input) {
            const fileName = input.files[0] ? input.files[0].name : '';
            document.getElementById('fileName').textContent = fileName;
        }
        
        // Drag and drop support
        const dropZone = document.getElementById('dropZone');
        const csvInput = document.getElementById('csvFile');
        
        dropZone.addEventListener('click', function() { csvInput.click(); });
        dropZone.addEventListener('dragover', function(e) { 
            e.preventDefault(); 
            this.classList.add('drag-over'); 
        });
        dropZone.addEventListener('dragleave', function() { 
            this.classList.remove('drag-over'); 
        });
        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('drag-over');
            if (e.dataTransfer.files.length > 0) {
                csvInput.files = e.dataTransfer.files;
                updateFileName(csvInput);
            }
        });
        
        // Search/filter students
        function filterStudents() {
            const query = document.getElementById('studentSearch').value.toLowerCase();
            const rows = document.querySelectorAll('#studentTable tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? '' : 'none';
            });
        }
    </script>
</body>
</html>
