package com.speeda.Core.service;
import com.speeda.Core.dto.StrategyDTO;
import com.speeda.Core.mapper.StrategyMapper;
import com.speeda.Core.model.Session;
import com.speeda.Core.model.Strategy;
import com.speeda.Core.repository.SessionRepository;
import com.speeda.Core.repository.StrategyRepository;
import com.speeda.Core.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyService implements IStrategyService {
    private final StrategyRepository strategyRepository;
    private final SessionRepository sessionRepository;
    private final StrategyMapper strategyMapper;
    private final UserContext userContext;

    @Override
    public StrategyDTO getStrategy(Long id) {
        return strategyRepository.findById(id)
                .map(strategyMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Strategy not found"));
    }

    @Override
    public List<StrategyDTO> getAllStrategies() {
        return strategyRepository.findAll().stream()
                .map(strategyMapper::toDto)
                .toList();
    }

    @Override
    public StrategyDTO getStrategyBySessionId(Long sessionId) {
        Long userId = userContext.getCurrentUserId();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: This session does not belong to you");
        }
        return strategyRepository.findBySessionId(sessionId)
                .map(strategyMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Strategy not found for this session"));
    }
}
