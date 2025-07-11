package com.speeda.Core.service;

import com.speeda.Core.dto.StrategyDTO;
import com.speeda.Core.model.Strategy;

import java.util.List;
import java.util.List;

public interface IStrategyService {
    StrategyDTO getStrategy(Long id);
    List<StrategyDTO> getAllStrategies();
    StrategyDTO getStrategyBySessionId(Long sessionId);
}
