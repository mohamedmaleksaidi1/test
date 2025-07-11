package com.speeda.Core.mapper;

import com.speeda.Core.dto.PreferenceDTO;
import com.speeda.Core.model.Preference;
import org.mapstruct.*;



@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PreferenceMapper {
    Preference toEntity(PreferenceDTO dto);
    PreferenceDTO toDto(Preference entity);

    void updatePreferenceFromDto(PreferenceDTO dto, @MappingTarget Preference entity);
}
