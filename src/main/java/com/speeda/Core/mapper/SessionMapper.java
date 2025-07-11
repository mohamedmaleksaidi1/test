package com.speeda.Core.mapper;

import com.speeda.Core.dto.SessionDTO;
import com.speeda.Core.model.Session;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SessionMapper {
    Session toEntity(SessionDTO dto);
    SessionDTO toDto(Session entity);

    void updateSessionFromDto(SessionDTO dto, @MappingTarget Session entity);
}
