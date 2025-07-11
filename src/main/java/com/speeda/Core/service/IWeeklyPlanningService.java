package com.speeda.Core.service;

import com.speeda.Core.dto.WeeklyPlanningDTO;
import com.speeda.Core.model.WeeklyPlanning;

import java.util.List;
import java.util.List;

import java.util.List;

public interface IWeeklyPlanningService {
    WeeklyPlanningDTO getWeeklyPlanning(Long id);
    List<WeeklyPlanningDTO> getAllWeeklyPlannings();
}
