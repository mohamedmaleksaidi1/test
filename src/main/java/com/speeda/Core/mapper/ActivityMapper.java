package com.speeda.Core.mapper;

import com.speeda.Core.dto.ActivityDTO;
import com.speeda.Core.model.Activity;
import org.mapstruct.*;
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ActivityMapper {
    Activity toEntity(ActivityDTO dto);
    ActivityDTO toDto(Activity entity);
    void updateActivityFromDto(ActivityDTO dto, @MappingTarget Activity entity);
}
