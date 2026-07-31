# Smart Exam Seating & Anti-Cheating Allocator

A full-stack Java web application that automatically arranges exam seating so no two adjacent students share the same subject or class, preventing copying. Includes an invigilator duty auto-scheduler.

## Tech Stack

- **Backend:** Java Servlets + JSP (Jakarta Servlet 6.0)
- **Database:** MySQL with raw JDBC (DAO pattern)
- **Server:** Apache Tomcat 10.1
- **Frontend:** HTML, CSS, vanilla JavaScript, Bootstrap 5 CDN
- **IDE:** Eclipse Dynamic Web Project

---

## Setup Instructions

### 1. Prerequisites

- **Java JDK 24** installed (project compiler level is Java 24)
- **Eclipse IDE for Enterprise Java Developers** (with WTP)
- **Apache Tomcat 10.1** installed and configured in Eclipse
- **MySQL 8.x** server running
- **MySQL Connector/J** jar file ([download here](https://dev.mysql.com/downloads/connector/j/))

### 2. Import Project into Eclipse

1. Open Eclipse → `File` → `Import`
2. Select `General` → `Existing Projects into Workspace`
3. Browse to the project root folder
4. Check the project and click `Finish`
5. If Eclipse doesn't recognize it as a Dynamic Web Project, right-click the project → `Properties` → `Project Facets` → ensure `Dynamic Web Module 6.0` and `Java` are checked

### 3. Set Up MySQL Database

1. Open MySQL command line or Workbench
2. Run the schema file:
   ```sql
   source /path/to/project/database/schema.sql
   ```
   This creates the `exam_seating_db` database with all tables and sample data.

3. Update database credentials in `src/db.properties` (deployed to `WEB-INF/classes`):
   ```properties
   db.url=jdbc:mysql://localhost:3306/exam_seating_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=YOUR_MYSQL_PASSWORD
   ```

### 4. Add MySQL JDBC Driver

1. Use the included `mysql-connector-j-9.6.0.jar`, or download a compatible MySQL Connector/J jar from the MySQL website
2. Keep the jar file in `WebContent/WEB-INF/lib/`
3. In Eclipse, right-click the project → `Build Path` → `Configure Build Path` → `Libraries` → `Add JARs` → select `WebContent/WEB-INF/lib/mysql-connector-j-9.6.0.jar`

### 5. Configure Tomcat in Eclipse

1. `Window` → `Preferences` → `Server` → `Runtime Environments` → `Add`
2. Select `Apache Tomcat v10.1` → browse to your Tomcat installation directory
3. Right-click the project → `Properties` → `Targeted Runtimes` → check `Apache Tomcat v10.1`

### 6. Run the Application

1. Right-click the project → `Run As` → `Run on Server`
2. Select your Tomcat server → `Finish`
3. Open `http://localhost:8082/ExamSeatingAllocator/` in your browser (or your configured Tomcat HTTP port)
4. Login with default credentials: **admin** / **admin123**

---

## Default Login Credentials

| Username | Password |
|----------|----------|
| admin    | admin123 |

---

## Project Structure

```
Smart_Exam_Seating_Anti_Cheating_Allocator/
├── src/
│   ├── db.properties   → Runtime database config copied to WEB-INF/classes
│   └── com/examseating/
│       ├── model/      → POJOs (Student, Room, ExamSession, etc.)
│       ├── dao/        → DAO interfaces + JDBC implementations
│       ├── algorithm/  → SeatAllocator, DutyScheduler
│       ├── servlet/    → All HTTP servlets
│       ├── filter/     → AuthFilter for /admin/* protection
│       └── util/       → DBConnection, ConstraintChecker, PasswordUtil
├── WebContent/
│   ├── WEB-INF/
│   │   ├── web.xml           → Servlet mappings
│   │   ├── db.properties     → Database config copy
│   │   └── lib/              → MySQL connector jar
│   ├── css/style.css         → Complete dark-themed design system
│   ├── *.jsp                 → All JSP pages
│   └── sidebar.jsp           → Reusable sidebar navigation
├── database/
│   └── schema.sql            → Full schema + sample data
└── README.md
```

---

## How the Seating Algorithm Works

### The Problem
In traditional exam seating, students from the same class sit together, making it easy to copy. We need to arrange seats so that **no two adjacent students have the same exam paper**.

### The Algorithm (Graph Coloring Approach)

Think of it like coloring a grid map — adjacent regions can't share colors.

**Step 1 — Group by Subject:**
All students are separated into queues by their `subject_code`. For example: CS301 queue, EC401 queue, ME201 queue, CE101 queue.

**Step 2 — Interleave (Round-Robin):**
We create a single candidate list by picking one student from each queue in rotation:
```
CS301, EC401, ME201, CE101, CS301, EC401, ME201, CE101, ...
```
This naturally spreads subjects apart before we even start placing.

**Step 3 — Snake-Order Traversal:**
We fill the room grid in a zigzag pattern:
```
Row 0: →  →  →  →  →  (left to right)
Row 1: ←  ←  ←  ←  ←  (right to left)
Row 2: →  →  →  →  →  (left to right)
```
This keeps the "last placed" student always adjacent, making constraint checking efficient.

**Step 4 — Constraint Check (isSafe):**
For each seat, we check 5 neighbors:
- Left, Right, Top, Diagonal-Top-Left, Diagonal-Top-Right

If any neighbor has the **same subject_code** → REJECT that student for this seat, try the next candidate.

**Step 5 — Fallback Strategy:**
- First try: strict check (subject + class year must differ)
- Second try: relaxed check (only subject must differ)
- Last resort: force-place (should rarely happen with 3+ subjects)

**Step 6 — Multi-Room:**
When one room fills up, continue with the next room using the remaining student pool.

### Time Complexity
`O(S × C)` where S = total seats, C = average candidates checked per seat. In practice, C is small (2-3) because interleaving keeps subjects well-distributed.

### Interview Analogy
"It's like the N-Queens problem — each seat is a position, each student is a queen, and the constraint is that adjacent seats can't have the same exam paper. We use a greedy approach with backtracking."

---

## Invigilator Duty Scheduler

Uses a **greedy fairness algorithm**:
1. Sort invigilators by duty count (ascending)
2. For each room, assign the invigilator with the fewest duties who isn't already assigned in this time slot
3. This ensures even distribution — no one is overloaded

---

## Features

- ✅ Admin login with SHA-256 password hashing
- ✅ Session-based authentication with AuthFilter
- ✅ Room management (CRUD with capacity auto-calculation)
- ✅ Exam session management
- ✅ Student upload (single + CSV bulk upload)
- ✅ Anti-cheating seating allocation algorithm
- ✅ Visual seating chart with color-coded subjects
- ✅ Print-friendly seating charts (`@media print`)
- ✅ Public student seat lookup (no login required)
- ✅ Invigilator management + auto-duty assignment
- ✅ Dashboard with statistics and workflow guide
- ✅ Input validation (client-side + server-side)
- ✅ Dark premium UI with glassmorphism design
- ✅ Responsive layout for mobile/tablet
- ✅ Empty-state UI (no blank pages)

---

## Sample CSV Format

```csv
roll_no,name,branch,class_year,subject_code
CS2024001,Aarav Sharma,CSE,2nd Year,CS301
EC2024001,Amit Kumar,ECE,2nd Year,EC401
ME2024001,Rajesh Pandey,MECH,3rd Year,ME201
```

---

## Verification

After running allocation, execute this SQL query to verify no constraint violations exist:

```sql
SELECT a1.allocation_id, s1.roll_no, s1.subject_code, 
       a2.allocation_id, s2.roll_no, s2.subject_code
FROM seat_allocation a1
JOIN seat_allocation a2 ON a1.exam_id = a2.exam_id 
    AND a1.room_id = a2.room_id
    AND a1.allocation_id < a2.allocation_id
JOIN students s1 ON a1.student_id = s1.student_id
JOIN students s2 ON a2.student_id = s2.student_id
WHERE s1.subject_code = s2.subject_code
  AND (
    (a1.seat_row = a2.seat_row AND ABS(a1.seat_col - a2.seat_col) = 1)
    OR (a1.seat_col = a2.seat_col AND ABS(a1.seat_row - a2.seat_row) = 1)
    OR (ABS(a1.seat_row - a2.seat_row) = 1 AND ABS(a1.seat_col - a2.seat_col) = 1)
  );
```

**If this returns 0 rows**, the allocation is correct — no adjacent students share the same subject.
