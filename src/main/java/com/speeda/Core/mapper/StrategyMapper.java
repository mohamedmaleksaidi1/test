package com.speeda.Core.mapper;

import com.speeda.Core.dto.StrategyDTO;
import com.speeda.Core.model.Strategy;
import org.mapstruct.*;


import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StrategyMapper {
    @Mapping(source = "session.id", target = "sessionId")
    StrategyDTO toDto(Strategy entity);

    @Mapping(source = "sessionId", target = "session.id")
    Strategy toEntity(StrategyDTO dto);

}

