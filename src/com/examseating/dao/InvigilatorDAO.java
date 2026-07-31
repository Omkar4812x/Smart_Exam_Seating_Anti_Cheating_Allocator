package com.examseating.dao;

import com.examseating.model.Invigilator;
import java.util.List;

public interface InvigilatorDAO {
    void addInvigilator(Invigilator invigilator);
    void deleteInvigilator(int invigilatorId);
    Invigilator getInvigilatorById(int invigilatorId);
    List<Invigilator> getAllInvigilators();
    int getInvigilatorCount();
}
