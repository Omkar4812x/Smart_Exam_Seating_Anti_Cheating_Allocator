-- ============================================================
-- Smart Exam Seating & Anti-Cheating Allocator
-- Database Schema + Sample Data
-- ============================================================

CREATE DATABASE IF NOT EXISTS exam_seating_db;
USE exam_seating_db;

-- -----------------------------------------------------------
-- Table: students
-- -----------------------------------------------------------
CREATE TABLE students (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  roll_no VARCHAR(20) NOT NULL,
  name VARCHAR(100),
  branch VARCHAR(50),
  class_year VARCHAR(20),
  subject_code VARCHAR(20)
);

-- -----------------------------------------------------------
-- Table: rooms
-- -----------------------------------------------------------
CREATE TABLE rooms (
  room_id INT PRIMARY KEY AUTO_INCREMENT,
  room_no VARCHAR(20),
  rows_count INT,
  cols_count INT,
  capacity INT
);

-- -----------------------------------------------------------
-- Table: exam_sessions
-- -----------------------------------------------------------
CREATE TABLE exam_sessions (
  exam_id INT PRIMARY KEY AUTO_INCREMENT,
  exam_name VARCHAR(100),
  exam_date DATE,
  exam_time VARCHAR(50)
);

-- -----------------------------------------------------------
-- Table: seat_allocation
-- -----------------------------------------------------------
CREATE TABLE seat_allocation (
  allocation_id INT PRIMARY KEY AUTO_INCREMENT,
  exam_id INT,
  student_id INT,
  room_id INT,
  seat_row INT,
  seat_col INT,
  FOREIGN KEY (exam_id) REFERENCES exam_sessions(exam_id),
  FOREIGN KEY (student_id) REFERENCES students(student_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- -----------------------------------------------------------
-- Table: invigilators
-- -----------------------------------------------------------
CREATE TABLE invigilators (
  invigilator_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  department VARCHAR(50)
);

-- -----------------------------------------------------------
-- Table: duty_schedule
-- -----------------------------------------------------------
CREATE TABLE duty_schedule (
  duty_id INT PRIMARY KEY AUTO_INCREMENT,
  exam_id INT,
  room_id INT,
  invigilator_id INT,
  duty_slot VARCHAR(50),
  FOREIGN KEY (exam_id) REFERENCES exam_sessions(exam_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id),
  FOREIGN KEY (invigilator_id) REFERENCES invigilators(invigilator_id)
);

-- -----------------------------------------------------------
-- Table: admin_users
-- -----------------------------------------------------------
CREATE TABLE admin_users (
  admin_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE,
  password VARCHAR(100)
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Admin user: username = admin, password = admin123
-- SHA-256 hash of "admin123" = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
INSERT INTO admin_users (username, password) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- Sample rooms
INSERT INTO rooms (room_no, rows_count, cols_count, capacity) VALUES
('A101', 5, 6, 30),
('A102', 4, 5, 20),
('B201', 6, 6, 36);

-- Sample exam sessions
INSERT INTO exam_sessions (exam_name, exam_date, exam_time) VALUES
('Mid-Semester Examination 2026', '2026-08-15', '10:00 AM - 01:00 PM'),
('End-Semester Examination 2026', '2026-12-10', '02:00 PM - 05:00 PM');

-- Sample students (4 subjects, spread across branches and class years)
-- Subject: CS301 - Data Structures
INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES
('CS2024001', 'Aarav Sharma', 'CSE', '2nd Year', 'CS301'),
('CS2024002', 'Priya Patel', 'CSE', '2nd Year', 'CS301'),
('CS2024003', 'Rohan Gupta', 'CSE', '2nd Year', 'CS301'),
('CS2024004', 'Sneha Reddy', 'CSE', '2nd Year', 'CS301'),
('CS2024005', 'Vikram Singh', 'CSE', '2nd Year', 'CS301'),
('CS2024006', 'Ananya Iyer', 'CSE', '2nd Year', 'CS301'),
('CS2024007', 'Karan Mehta', 'CSE', '2nd Year', 'CS301'),
('CS2024008', 'Divya Nair', 'CSE', '2nd Year', 'CS301');

-- Subject: EC401 - Digital Electronics
INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES
('EC2024001', 'Amit Kumar', 'ECE', '2nd Year', 'EC401'),
('EC2024002', 'Nisha Verma', 'ECE', '2nd Year', 'EC401'),
('EC2024003', 'Rahul Joshi', 'ECE', '2nd Year', 'EC401'),
('EC2024004', 'Pooja Desai', 'ECE', '2nd Year', 'EC401'),
('EC2024005', 'Suresh Yadav', 'ECE', '2nd Year', 'EC401'),
('EC2024006', 'Megha Kapoor', 'ECE', '2nd Year', 'EC401'),
('EC2024007', 'Arjun Pillai', 'ECE', '2nd Year', 'EC401'),
('EC2024008', 'Tanvi Bhatt', 'ECE', '2nd Year', 'EC401');

-- Subject: ME201 - Thermodynamics
INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES
('ME2024001', 'Rajesh Pandey', 'MECH', '3rd Year', 'ME201'),
('ME2024002', 'Swati Mishra', 'MECH', '3rd Year', 'ME201'),
('ME2024003', 'Deepak Chauhan', 'MECH', '3rd Year', 'ME201'),
('ME2024004', 'Kavita Saxena', 'MECH', '3rd Year', 'ME201'),
('ME2024005', 'Manish Tiwari', 'MECH', '3rd Year', 'ME201'),
('ME2024006', 'Ritu Agarwal', 'MECH', '3rd Year', 'ME201'),
('ME2024007', 'Nikhil Dubey', 'MECH', '3rd Year', 'ME201'),
('ME2024008', 'Shreya Kulkarni', 'MECH', '3rd Year', 'ME201');

-- Subject: CE101 - Engineering Mechanics
INSERT INTO students (roll_no, name, branch, class_year, subject_code) VALUES
('CE2024001', 'Aditya Rao', 'CIVIL', '1st Year', 'CE101'),
('CE2024002', 'Neha Srivastava', 'CIVIL', '1st Year', 'CE101'),
('CE2024003', 'Sanjay Patil', 'CIVIL', '1st Year', 'CE101'),
('CE2024004', 'Pallavi Jain', 'CIVIL', '1st Year', 'CE101'),
('CE2024005', 'Gaurav Malhotra', 'CIVIL', '1st Year', 'CE101'),
('CE2024006', 'Ishita Bansal', 'CIVIL', '1st Year', 'CE101'),
('CE2024007', 'Tarun Sethi', 'CIVIL', '1st Year', 'CE101'),
('CE2024008', 'Meera Chopra', 'CIVIL', '1st Year', 'CE101');

-- Sample invigilators
INSERT INTO invigilators (name, department) VALUES
('Dr. Ramesh Kumar', 'CSE'),
('Prof. Sunita Sharma', 'ECE'),
('Dr. Anil Verma', 'MECH'),
('Prof. Geeta Mishra', 'CIVIL'),
('Dr. Sunil Patel', 'CSE');

-- ============================================================
-- VERIFICATION QUERY: Run after allocation to check constraints
-- This query finds adjacent seat pairs with the same subject_code
-- If it returns 0 rows, the allocation is valid.
-- ============================================================
/*
SELECT 
    a1.allocation_id AS seat1_id,
    s1.roll_no AS seat1_roll,
    s1.subject_code AS seat1_subject,
    a1.seat_row AS seat1_row,
    a1.seat_col AS seat1_col,
    a2.allocation_id AS seat2_id,
    s2.roll_no AS seat2_roll,
    s2.subject_code AS seat2_subject,
    a2.seat_row AS seat2_row,
    a2.seat_col AS seat2_col
FROM seat_allocation a1
JOIN seat_allocation a2 ON a1.exam_id = a2.exam_id 
    AND a1.room_id = a2.room_id
    AND a1.allocation_id < a2.allocation_id
JOIN students s1 ON a1.student_id = s1.student_id
JOIN students s2 ON a2.student_id = s2.student_id
WHERE s1.subject_code = s2.subject_code
  AND (
    -- Left/Right neighbors (same row, adjacent column)
    (a1.seat_row = a2.seat_row AND ABS(a1.seat_col - a2.seat_col) = 1)
    -- Top/Bottom neighbors (same column, adjacent row)
    OR (a1.seat_col = a2.seat_col AND ABS(a1.seat_row - a2.seat_row) = 1)
    -- Diagonal neighbors
    OR (ABS(a1.seat_row - a2.seat_row) = 1 AND ABS(a1.seat_col - a2.seat_col) = 1)
  );
*/
