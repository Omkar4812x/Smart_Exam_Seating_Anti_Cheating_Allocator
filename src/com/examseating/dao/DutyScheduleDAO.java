package com.examseating.dao;

import com.examseating.model.DutySchedule;
import java.util.List;

public interface DutyScheduleDAO {
    void saveDuties(List<DutySchedule> duties);
    List<DutySchedule> getDutiesByExam(int examId);
    void deleteDutiesByExam(int examId);
    boolean hasDuties(int examId);
}
