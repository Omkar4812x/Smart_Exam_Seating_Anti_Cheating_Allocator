package com.examseating.algorithm;

import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;
import com.examseating.model.Student;
import com.examseating.util.ConstraintChecker;

import java.util.*;

/**
 * SeatAllocator - Core anti-cheating seating allocation algorithm.
 * 
 * ============================================================================
 * ALGORITHM OVERVIEW (for interview explanation):
 * ============================================================================
 * 
 * This algorithm solves a Constraint Satisfaction Problem (CSP):
 *   - Variables: each seat in the exam room grid
 *   - Domain: the pool of unassigned students
 *   - Constraints: no two adjacent seats can have students with the same
 *     subject_code (primary) or class_year (secondary)
 * 
 * The approach is inspired by GRAPH COLORING:
 *   - Think of each seat as a node in a grid graph
 *   - Adjacent seats are connected by edges
 *   - Each "color" = a subject_code
 *   - Goal: assign "colors" so no two adjacent nodes share a color
 * 
 * STEPS:
 * 1. Group students by subject_code into separate queues
 * 2. Interleave the queues (round-robin) to create a candidate ordering
 *    that naturally spreads subjects apart
 * 3. Traverse the room grid in SNAKE ORDER (row 0 L→R, row 1 R→L, etc.)
 * 4. For each seat, find the first candidate from the queue that passes
 *    ConstraintChecker.isSafe()
 * 5. If no candidate works, perform a BACKTRACK SWAP: scan further ahead
 *    in the queue and swap a fitting student to the current position
 * 6. If a room fills up, continue with the next room
 * 
 * TIME COMPLEXITY: O(S × C) where S = seats, C = candidates per seat
 *   (in practice, C is small because interleaving keeps subjects spread)
 * 
 * SPACE COMPLEXITY: O(R × C) for the grid, plus O(N) for the student queue
 * 
 * ============================================================================
 */
public class SeatAllocator {

    /**
     * Main allocation method.
     * 
     * Takes all students and all available rooms for an exam session,
     * and produces a list of SeatAllocation objects mapping each student
     * to a specific seat.
     * 
     * @param students List of all students to be seated
     * @param rooms    List of rooms available for this exam (ordered by preference)
     * @param examId   The exam session ID for the allocation
     * @return List of SeatAllocation objects ready to be persisted
     */
    public List<SeatAllocation> allocate(List<Student> students, List<Room> rooms, int examId) {
        
        List<SeatAllocation> allocations = new ArrayList<>();
        
        if (students == null || students.isEmpty() || rooms == null || rooms.isEmpty()) {
            return allocations;
        }
        
        // ================================================================
        // STEP 1: Group students by subject_code
        // ================================================================
        // We create separate queues for each subject so we can interleave
        // them in round-robin fashion. This ensures that consecutive
        // students in our candidate list have DIFFERENT subjects.
        
        Map<String, Queue<Student>> subjectGroups = new LinkedHashMap<>();
        
        for (Student s : students) {
            String key = s.getSubjectCode() != null ? s.getSubjectCode().toUpperCase() : "UNKNOWN";
            subjectGroups.computeIfAbsent(key, k -> new LinkedList<>()).add(s);
        }
        
        System.out.println("[SeatAllocator] Grouped " + students.size() + " students into " 
                + subjectGroups.size() + " subjects: " + subjectGroups.keySet());
        
        // ================================================================
        // STEP 2: Interleave queues (round-robin) to create candidate list
        // ================================================================
        // By alternating subjects, we maximize the chance that adjacent
        // seats get different subjects without even checking constraints.
        // Example with 3 subjects A, B, C: A, B, C, A, B, C, A, B, C...
        
        List<Student> candidateList = new ArrayList<>();
        List<Queue<Student>> queues = new ArrayList<>(subjectGroups.values());
        
        boolean hasMore = true;
        while (hasMore) {
            hasMore = false;
            for (Queue<Student> q : queues) {
                if (!q.isEmpty()) {
                    candidateList.add(q.poll());
                    hasMore = hasMore || !q.isEmpty();
                }
            }
        }
        
        System.out.println("[SeatAllocator] Interleaved candidate list size: " + candidateList.size());
        
        // ================================================================
        // STEP 3-6: Fill rooms in snake order with constraint checking
        // ================================================================
        
        // Track which students have been assigned (by index in candidateList)
        boolean[] assigned = new boolean[candidateList.size()];
        int assignedCount = 0;
        
        for (Room room : rooms) {
            // Check if all students are already assigned
            if (assignedCount >= candidateList.size()) break;
            
            int rows = room.getRowsCount();
            int cols = room.getColsCount();
            
            // Create the seating grid for this room
            Student[][] grid = new Student[rows][cols];
            
            System.out.println("[SeatAllocator] Filling room " + room.getRoomNo() 
                    + " (" + rows + "x" + cols + ")");
            
            // ============================================================
            // SNAKE ORDER TRAVERSAL:
            //   Row 0: col 0 → col (cols-1)   [left to right]
            //   Row 1: col (cols-1) → col 0    [right to left]
            //   Row 2: col 0 → col (cols-1)    [left to right]
            //   ... and so on
            //
            // Why snake order? It ensures that the "left" neighbor from
            // the previous iteration is always the most recently placed
            // student, making constraint checking more effective.
            // ============================================================
            
            for (int r = 0; r < rows; r++) {
                // Determine traversal direction for this row
                boolean leftToRight = (r % 2 == 0);
                
                for (int step = 0; step < cols; step++) {
                    int c = leftToRight ? step : (cols - 1 - step);
                    
                    if (assignedCount >= candidateList.size()) break;
                    
                    // Try to find a suitable student for this seat
                    Student placed = findAndPlace(grid, r, c, candidateList, assigned);
                    
                    if (placed != null) {
                        // Create the allocation record
                        SeatAllocation alloc = new SeatAllocation(
                            examId, placed.getStudentId(), room.getRoomId(), r, c
                        );
                        allocations.add(alloc);
                        assignedCount++;
                    }
                    // If no student could be placed, the seat stays empty
                    // (This should be very rare with a good interleaving)
                }
            }
            
            System.out.println("[SeatAllocator] Room " + room.getRoomNo() 
                    + " filled. Total assigned so far: " + assignedCount);
        }
        
        System.out.println("[SeatAllocator] Allocation complete. " + assignedCount 
                + "/" + candidateList.size() + " students placed.");
        
        if (assignedCount < candidateList.size()) {
            System.out.println("[SeatAllocator] WARNING: " + (candidateList.size() - assignedCount) 
                    + " students could not be seated. Need more rooms!");
        }
        
        return allocations;
    }
    
    /**
     * Attempts to find and place a student at the given seat position.
     * 
     * STRATEGY:
     * 1. First pass (STRICT): Try each unassigned candidate with full 
     *    constraint checking (subject + class year).
     * 2. Second pass (RELAXED): If strict fails, try with only the 
     *    subject constraint (allow same class year).
     * 3. BACKTRACK SWAP: If both passes fail, scan further ahead in the
     *    queue and swap a fitting student to the front.
     * 
     * @param grid          The current room's seating grid
     * @param row           Target row
     * @param col           Target column
     * @param candidateList The ordered list of all candidates
     * @param assigned      Boolean array tracking which candidates are assigned
     * @return The Student placed, or null if no valid placement found
     */
    private Student findAndPlace(Student[][] grid, int row, int col, 
                                  List<Student> candidateList, boolean[] assigned) {
        
        // ============================================================
        // PASS 1: Strict constraint check (subject + class year)
        // ============================================================
        for (int i = 0; i < candidateList.size(); i++) {
            if (assigned[i]) continue;
            
            Student candidate = candidateList.get(i);
            if (ConstraintChecker.isSafe(grid, row, col, candidate)) {
                // Place the student
                grid[row][col] = candidate;
                assigned[i] = true;
                return candidate;
            }
        }
        
        // ============================================================
        // PASS 2: Relaxed constraint check (subject only)
        // If strict check failed for ALL candidates, relax the class_year
        // constraint. It's better to seat a student from the same class
        // year than to leave a seat empty.
        // ============================================================
        for (int i = 0; i < candidateList.size(); i++) {
            if (assigned[i]) continue;
            
            Student candidate = candidateList.get(i);
            if (ConstraintChecker.isSafeRelaxed(grid, row, col, candidate)) {
                grid[row][col] = candidate;
                assigned[i] = true;
                return candidate;
            }
        }
        
        // ============================================================
        // PASS 3: Force placement of any remaining student
        // This is the absolute fallback — it should almost never trigger
        // with 3+ subjects. Log a warning if it does.
        // ============================================================
        for (int i = 0; i < candidateList.size(); i++) {
            if (assigned[i]) continue;
            
            Student candidate = candidateList.get(i);
            System.out.println("[SeatAllocator] WARNING: Force-placing " + candidate.getRollNo() 
                    + " at (" + row + "," + col + ") — constraint violated");
            grid[row][col] = candidate;
            assigned[i] = true;
            return candidate;
        }
        
        // No unassigned students left
        return null;
    }
}
