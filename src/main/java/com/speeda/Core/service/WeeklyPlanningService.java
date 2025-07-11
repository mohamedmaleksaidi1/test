package com.speeda.Core.service;
import com.speeda.Core.dto.WeeklyPlanningDTO;
import com.speeda.Core.mapper.WeeklyPlanningMapper;
import com.speeda.Core.model.WeeklyPlanning;
import com.speeda.Core.repository.WeeklyPlanningRepository;
import com.speeda.Core.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyPlanningService implements IWeeklyPlanningService {

    private final WeeklyPlanningRepository weeklyPlanningRepository;
    private final WeeklyPlanningMapper weeklyPlanningMapper;
    private final UserContext userContext;

    @Override
    public WeeklyPlanningDTO getWeeklyPlanning(Long id) {
        Long userId = userContext.getCurrentUserId();
        WeeklyPlanning wp = weeklyPlanningRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("WeeklyPlanning not found or not authorized"));
        return weeklyPlanningMapper.toDto(wp);
    }

    @Override
    public List<WeeklyPlanningDTO> getAllWeeklyPlannings() {
        Long userId = userContext.getCurrentUserId();
        return weeklyPlanningRepository.findAllByUserId(userId).stream()
                .map(weeklyPlanningMapper::toDto)
                .toList();
    }
}
