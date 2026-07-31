package com.examseating.dao;

import com.examseating.model.SeatAllocation;
import java.util.List;

/**
 * AllocationDAO - Data Access Object interface for seat allocations.
 */
public interface AllocationDAO {
    
    /** Save a batch of seat allocations for an exam */
    void saveAllocations(List<SeatAllocation> allocations);
    
    /** Get all allocations for a given exam (with student/room details) */
    List<SeatAllocation> getAllocationsByExam(int examId);
    
    /** Get allocations for a specific room in an exam */
    List<SeatAllocation> getAllocationsByRoom(int examId, int roomId);
    
    /** Delete all allocations for an exam (before re-running) */
    void deleteAllocationsByExam(int examId);
    
    /** Find a student's seat allocation by roll number and exam */
    SeatAllocation findByRollNoAndExam(String rollNo, int examId);
    
    /** Check if allocations exist for an exam */
    boolean hasAllocations(int examId);
}
