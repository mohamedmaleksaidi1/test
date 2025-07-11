package com.speeda.Core.mapper;

import com.speeda.Core.dto.WeeklyPlanningDTO;
import com.speeda.Core.model.WeeklyPlanning;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WeeklyPlanningMapper {
    WeeklyPlanning toEntity(WeeklyPlanningDTO dto);
    WeeklyPlanningDTO toDto(WeeklyPlanning entity);

    void updateWeeklyPlanningFromDto(WeeklyPlanningDTO dto, @MappingTarget WeeklyPlanning entity);
}
