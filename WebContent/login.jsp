<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Exam Seating Allocator</title>
    <meta name="description" content="Admin login for the Smart Exam Seating & Anti-Cheating Allocator">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&family=Outfit:wght@500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/css/style.css" rel="stylesheet">
    <style>
        /* Embedded Split-Screen Login Styles for immediate live refresh */
        body.login-split-page {
            background-color: #161320 !important;
            background-image: 
                radial-gradient(at 10% 20%, rgba(124, 58, 237, 0.22) 0px, transparent 50%),
                radial-gradient(at 90% 80%, rgba(99, 102, 241, 0.18) 0px, transparent 50%) !important;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
            font-family: 'Plus Jakarta Sans', sans-serif;
            margin: 0;
        }

        .login-split-wrapper {
            width: 100%;
            max-width: 1060px;
        }

        .login-split-card {
            background: #14121d;
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 28px;
            padding: 16px;
            display: flex;
            box-shadow: 0 30px 90px rgba(0, 0, 0, 0.7), inset 0 1px 0 rgba(255, 255, 255, 0.12);
            min-height: 600px;
        }

        /* Left Hero Panel */
        .login-hero-panel {
            flex: 1.1;
            min-width: 440px;
            background: url('<%=request.getContextPath()%>/images/login_hero.png') center/cover no-repeat;
            background-color: #271f47;
            border-radius: 22px;
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 36px;
            overflow: hidden;
        }

        .login-hero-panel .hero-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(180deg, rgba(28, 22, 51, 0.35) 0%, rgba(14, 11, 26, 0.88) 100%);
            z-index: 1;
        }

        .hero-top-bar, .hero-bottom-content {
            position: relative;
            z-index: 2;
        }

        .hero-top-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .hero-brand {
            display: flex;
            align-items: center;
            gap: 10px;
            font-family: 'Outfit', sans-serif;
            font-size: 24px;
            font-weight: 800;
            color: #ffffff;
            letter-spacing: -0.02em;
        }

        .hero-brand i {
            font-size: 26px;
            color: #a78bfa;
        }

        .btn-hero-website {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 8px 18px;
            background: rgba(255, 255, 255, 0.15);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 999px;
            color: #ffffff !important;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .btn-hero-website:hover {
            background: rgba(255, 255, 255, 0.28);
            transform: translateX(3px);
        }

        .hero-bottom-content h2 {
            font-family: 'Outfit', sans-serif;
            font-size: 28px;
            font-weight: 800;
            color: #ffffff;
            line-height: 1.2;
            margin-bottom: 8px;
        }

        .hero-bottom-content p {
            font-size: 14px;
            color: rgba(255, 255, 255, 0.75);
            margin-bottom: 22px;
        }

        .hero-indicators {
            display: flex;
            gap: 8px;
        }

        .hero-indicators .dot {
            height: 4px;
            width: 24px;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 2px;
        }

        .hero-indicators .dot.active {
            width: 48px;
            background: #ffffff;
        }

        /* Right Form Panel */
        .login-form-panel {
            flex: 1;
            padding: 44px 50px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .form-panel-header {
            margin-bottom: 30px;
        }

        .form-panel-header h1 {
            font-size: 32px;
            font-weight: 800;
            color: #ffffff;
            margin-bottom: 6px;
            font-family: 'Outfit', sans-serif;
        }

        .form-panel-header p {
            font-size: 14px;
            color: #94a3b8;
        }

        .form-group-split {
            margin-bottom: 22px;
        }

        .form-group-split label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: #94a3b8;
            margin-bottom: 8px;
        }

        .input-with-icon {
            position: relative;
            display: flex;
            align-items: center;
        }

        .input-with-icon i.bi-person,
        .input-with-icon i.bi-lock {
            position: absolute;
            left: 16px;
            font-size: 18px;
            color: #64748b;
            pointer-events: none;
        }

        .input-with-icon input {
            width: 100%;
            padding: 14px 46px;
            background: rgba(255, 255, 255, 0.04);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: 12px;
            color: #ffffff;
            font-size: 14px;
            font-family: 'Plus Jakarta Sans', sans-serif;
            transition: all 0.3s ease;
            outline: none;
        }

        .input-with-icon input:focus {
            border-color: #8b5cf6;
            box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.25);
            background: rgba(255, 255, 255, 0.08);
        }

        .btn-toggle-pw {
            position: absolute;
            right: 14px;
            background: none;
            border: none;
            color: #64748b;
            font-size: 18px;
            cursor: pointer;
            padding: 4px;
            transition: all 0.2s ease;
        }

        .btn-toggle-pw:hover {
            color: #a78bfa;
        }

        .form-options-split {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 26px;
            font-size: 13px;
        }

        .remember-me {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #94a3b8;
        }

        .remember-me input {
            accent-color: #8b5cf6;
        }

        .default-badge-pill {
            background: rgba(139, 92, 246, 0.15);
            border: 1px solid rgba(139, 92, 246, 0.3);
            color: #c4b5fd;
            padding: 4px 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
        }

        .btn-split-primary {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
            color: #ffffff;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 700;
            font-family: 'Plus Jakarta Sans', sans-serif;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
            box-shadow: 0 8px 24px rgba(109, 40, 217, 0.35);
        }

        .btn-split-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 14px 32px rgba(109, 40, 217, 0.5);
            background: linear-gradient(135deg, #9333ea 0%, #7c3aed 100%);
        }

        .form-panel-footer {
            margin-top: 30px;
            text-align: center;
        }

        .divider-text {
            position: relative;
            text-align: center;
            margin-bottom: 20px;
        }

        .divider-text::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            height: 1px;
            background: rgba(255, 255, 255, 0.08);
        }

        .divider-text span {
            position: relative;
            background: #14121d;
            padding: 0 14px;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 0.1em;
            color: #64748b;
        }

        .btn-split-secondary {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            padding: 12px;
            background: rgba(255, 255, 255, 0.04);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            color: #e2e8f0 !important;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .btn-split-secondary:hover {
            background: rgba(255, 255, 255, 0.08);
            border-color: rgba(255, 255, 255, 0.2);
        }

        @media (max-width: 991.98px) {
            .login-split-card {
                flex-direction: column;
            }
            .login-hero-panel {
                min-height: 260px;
                min-width: 100%;
            }
            .login-form-panel {
                padding: 32px 20px;
            }
        }
    </style>
</head>
<body class="login-split-page">
    <div class="login-split-wrapper">
        <div class="login-split-card">
            
            <!-- Left Side Artwork Hero Panel -->
            <div class="login-hero-panel">
                <div class="hero-overlay"></div>
                
                <!-- Top Header Bar inside Left Panel -->
                <div class="hero-top-bar">
                    <div class="hero-brand">
                        <i class="bi bi-shield-check"></i>
                        <span>ExamSeat</span>
                    </div>
                    <a href="<%=request.getContextPath()%>/student-lookup" class="btn-hero-website">
                        Student Lookup <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
                
                <!-- Bottom Content inside Left Panel -->
                <div class="hero-bottom-content">
                    <h2>Smart Exam Seating & Anti-Cheating Allocator</h2>
                    <p>Automated Constraint Solver & Invigilator Scheduler</p>
                    <div class="hero-indicators">
                        <span class="dot"></span>
                        <span class="dot"></span>
                        <span class="dot active"></span>
                    </div>
                </div>
            </div>
            
            <!-- Right Side Form Panel -->
            <div class="login-form-panel">
                <div class="form-panel-header">
                    <h1>Sign in to account</h1>
                    <p>Enter your credentials to access the admin dashboard</p>
                </div>
                
                <% if (errorMsg != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMsg %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <form action="<%=request.getContextPath()%>/login" method="post" id="splitLoginForm">
                    <div class="form-group-split">
                        <label for="username">Username</label>
                        <div class="input-with-icon">
                            <i class="bi bi-person"></i>
                            <input type="text" id="username" name="username" placeholder="Enter username" required autofocus>
                        </div>
                    </div>
                    
                    <div class="form-group-split">
                        <label for="password">Password</label>
                        <div class="input-with-icon">
                            <i class="bi bi-lock"></i>
                            <input type="password" id="password" name="password" placeholder="Enter password" required>
                            <button type="button" class="btn-toggle-pw" onclick="togglePasswordVisibility()">
                                <i class="bi bi-eye" id="pwIcon"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div class="form-options-split">
                        <label class="remember-me">
                            <input type="checkbox" checked disabled>
                            <span>Secure Admin Access</span>
                        </label>
                        <span class="default-badge-pill">Default: <strong>admin</strong> / <strong>admin123</strong></span>
                    </div>
                    
                    <button type="submit" class="btn-split-primary">
                        Sign In <i class="bi bi-box-arrow-in-right ms-1"></i>
                    </button>
                </form>
                
                <div class="form-panel-footer">
                    <div class="divider-text">
                        <span>OR PUBLIC ACCESS</span>
                    </div>
                    <a href="<%=request.getContextPath()%>/student-lookup" class="btn-split-secondary">
                        <i class="bi bi-search me-2"></i> Find Student Exam Seat
                    </a>
                </div>
            </div>
            
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function togglePasswordVisibility() {
            const pwInput = document.getElementById('password');
            const pwIcon = document.getElementById('pwIcon');
            if (pwInput.type === 'password') {
                pwInput.type = 'text';
                pwIcon.classList.remove('bi-eye');
                pwIcon.classList.add('bi-eye-slash');
            } else {
                pwInput.type = 'password';
                pwIcon.classList.remove('bi-eye-slash');
                pwIcon.classList.add('bi-eye');
            }
        }
    </script>
</body>
</html>
