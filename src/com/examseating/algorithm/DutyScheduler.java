package com.examseating.algorithm;

import com.examseating.model.DutySchedule;
import com.examseating.model.Invigilator;
import com.examseating.model.Room;

import java.util.*;

/**
 * DutyScheduler - Automatically assigns invigilators to exam rooms fairly.
 * 
 * ============================================================================
 * ALGORITHM OVERVIEW (for interview explanation):
 * ============================================================================
 * 
 * This is a GREEDY ASSIGNMENT algorithm with FAIRNESS constraints:
 * 
 * CONSTRAINT: No invigilator can be assigned to two rooms in the same time slot
 * FAIRNESS:   Invigilators with the fewest total duties get priority
 * 
 * STEPS:
 * 1. Track a "duty count" per invigilator (starts at 0 for all)
 * 2. For each room that needs an invigilator in this exam slot:
 *    a. Sort available invigilators by duty count (ascending)
 *    b. Pick the one with the fewest duties who ISN'T already assigned
 *       in this time slot
 *    c. Assign them, increment their duty count
 * 3. Persist all assignments to the duty_schedule table
 * 
 * This ensures an even distribution of duties across all invigilators.
 * 
 * TIME COMPLEXITY: O(R × I) where R = rooms, I = invigilators
 * ============================================================================
 */
public class DutyScheduler {

    /**
     * Assigns invigilators to rooms for a given exam session.
     * 
     * @param rooms        List of rooms that need invigilators for this exam
     * @param invigilators List of all available invigilators
     * @param examId       The exam session ID
     * @param dutySlot     The time slot string (e.g., "10:00 AM - 01:00 PM")
     * @return List of DutySchedule assignments
     */
    public List<DutySchedule> assignDuties(List<Room> rooms, List<Invigilator> invigilators,
                                            int examId, String dutySlot) {
        
        List<DutySchedule> duties = new ArrayList<>();
        
        if (rooms == null || rooms.isEmpty() || invigilators == null || invigilators.isEmpty()) {
            return duties;
        }
        
        // ================================================================
        // STEP 1: Initialize duty count tracker
        // ================================================================
        // Maps invigilator ID → number of duties assigned so far
        // This enables the fairness rule: always prefer the person
        // with the fewest duties
        
        Map<Integer, Integer> dutyCounts = new HashMap<>();
        for (Invigilator inv : invigilators) {
            dutyCounts.put(inv.getInvigilatorId(), 0);
        }
        
        // Track who is already assigned in THIS time slot
        // (to prevent double-booking)
        Set<Integer> assignedInSlot = new HashSet<>();
        
        // ================================================================
        // STEP 2: For each room, pick the best available invigilator
        // ================================================================
        
        for (Room room : rooms) {
            // Sort invigilators by duty count (ascending) — those with 
            // fewer duties get priority for fairness
            List<Invigilator> sorted = new ArrayList<>(invigilators);
            sorted.sort(Comparator.comparingInt(
                inv -> dutyCounts.getOrDefault(inv.getInvigilatorId(), 0)
            ));
            
            // Find the first invigilator not yet assigned in this slot
            Invigilator chosen = null;
            for (Invigilator inv : sorted) {
                if (!assignedInSlot.contains(inv.getInvigilatorId())) {
                    chosen = inv;
                    break;
                }
            }
            
            if (chosen != null) {
                // Create the duty assignment
                DutySchedule duty = new DutySchedule();
                duty.setExamId(examId);
                duty.setRoomId(room.getRoomId());
                duty.setInvigilatorId(chosen.getInvigilatorId());
                duty.setDutySlot(dutySlot);
                duties.add(duty);
                
                // Update tracking
                assignedInSlot.add(chosen.getInvigilatorId());
                dutyCounts.merge(chosen.getInvigilatorId(), 1, Integer::sum);
                
                System.out.println("[DutyScheduler] Room " + room.getRoomNo() 
                        + " → " + chosen.getName() 
                        + " (total duties: " + dutyCounts.get(chosen.getInvigilatorId()) + ")");
            } else {
                System.out.println("[DutyScheduler] WARNING: No available invigilator for room " 
                        + room.getRoomNo() + " — all are busy in this slot!");
            }
        }
        
        System.out.println("[DutyScheduler] Assigned " + duties.size() 
                + " duties across " + rooms.size() + " rooms.");
        
        return duties;
    }
}
