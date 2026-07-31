package com.examseating.util;

import com.examseating.model.Student;

/**
 * ConstraintChecker - Validates seating constraints for the anti-cheating algorithm.
 * 
 * This utility class checks whether placing a student at a given seat position
 * violates any adjacency constraints. The core rule is:
 * 
 *   "No two adjacent seats (left, right, top, and diagonals) should have
 *    students from the same subject_code or same class_year."
 * 
 * The adjacency neighborhood for any seat at (row, col) consists of 5 positions:
 *   - Left:            (row, col-1)
 *   - Right:           (row, col+1)    [usually empty during forward placement]
 *   - Top:             (row-1, col)
 *   - Diagonal Top-Left:  (row-1, col-1)
 *   - Diagonal Top-Right: (row-1, col+1)
 * 
 * During allocation, we fill seats in snake order, so when placing a student:
 *   - Seats above (row-1) are already filled
 *   - The left/right seat in the current row may or may not be filled
 *     depending on traversal direction
 * 
 * INTERVIEW TIP: This is essentially a constraint satisfaction problem (CSP).
 * Each seat is a "variable" and each student is a possible "value."
 * The isSafe() method is the "constraint check" — similar to the 
 * "is it safe to place this queen?" check in the N-Queens problem.
 */
public class ConstraintChecker {

    /**
     * Checks whether placing the given student at position (row, col) in the
     * seating grid is safe — i.e., no neighbor has the same subject_code.
     * 
     * @param grid    The 2D seating grid (Student[rows][cols]). Null cells = empty seats.
     * @param row     The row index where we want to place the student (0-indexed).
     * @param col     The column index where we want to place the student (0-indexed).
     * @param student The student we are attempting to place.
     * @return true if the placement satisfies all constraints, false otherwise.
     */
    public static boolean isSafe(Student[][] grid, int row, int col, Student student) {
        int totalRows = grid.length;
        int totalCols = grid[0].length;
        
        // ============================================================
        // Check all 5 neighbor positions:
        //   1. Left        (row, col-1)
        //   2. Right       (row, col+1)
        //   3. Top         (row-1, col)
        //   4. Top-Left    (row-1, col-1)
        //   5. Top-Right   (row-1, col+1)
        // ============================================================
        
        // Direction offsets: {rowDelta, colDelta}
        int[][] neighbors = {
            {0, -1},   // Left
            {0, 1},    // Right
            {-1, 0},   // Top
            {-1, -1},  // Diagonal Top-Left
            {-1, 1}    // Diagonal Top-Right
        };
        
        for (int[] delta : neighbors) {
            int nRow = row + delta[0];
            int nCol = col + delta[1];
            
            // Bounds check — skip if neighbor is outside the grid
            if (nRow < 0 || nRow >= totalRows || nCol < 0 || nCol >= totalCols) {
                continue;
            }
            
            Student neighbor = grid[nRow][nCol];
            
            // Skip empty seats (not yet filled)
            if (neighbor == null) {
                continue;
            }
            
            // PRIMARY CONSTRAINT: Same subject_code → UNSAFE
            // Two students writing the same exam paper must NOT sit adjacent
            if (student.getSubjectCode() != null && 
                student.getSubjectCode().equalsIgnoreCase(neighbor.getSubjectCode())) {
                return false;
            }
            
            // SECONDARY CONSTRAINT: Same class_year → UNSAFE (softer constraint)
            // Students from the same class are more likely to know each other
            // and share answers, even across different subjects
            if (student.getClassYear() != null && 
                !student.getClassYear().isEmpty() &&
                student.getClassYear().equalsIgnoreCase(neighbor.getClassYear())) {
                return false;
            }
        }
        
        // All neighbor checks passed — placement is safe
        return true;
    }
    
    /**
     * A relaxed version of isSafe that only checks the PRIMARY constraint
     * (subject_code), ignoring the class_year constraint.
     * 
     * Used as a fallback when the strict check fails for all candidates —
     * it's better to place a student from the same class year than to
     * leave a seat empty or fail the allocation entirely.
     * 
     * @param grid    The 2D seating grid
     * @param row     Row index
     * @param col     Column index  
     * @param student The student to check
     * @return true if no neighbor shares the same subject_code
     */
    public static boolean isSafeRelaxed(Student[][] grid, int row, int col, Student student) {
        int totalRows = grid.length;
        int totalCols = grid[0].length;
        
        int[][] neighbors = {
            {0, -1}, {0, 1}, {-1, 0}, {-1, -1}, {-1, 1}
        };
        
        for (int[] delta : neighbors) {
            int nRow = row + delta[0];
            int nCol = col + delta[1];
            
            if (nRow < 0 || nRow >= totalRows || nCol < 0 || nCol >= totalCols) {
                continue;
            }
            
            Student neighbor = grid[nRow][nCol];
            if (neighbor == null) continue;
            
            // Only check subject_code (the hard constraint)
            if (student.getSubjectCode() != null && 
                student.getSubjectCode().equalsIgnoreCase(neighbor.getSubjectCode())) {
                return false;
            }
        }
        
        return true;
    }
}
