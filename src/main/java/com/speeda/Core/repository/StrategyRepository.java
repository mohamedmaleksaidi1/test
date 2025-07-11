package com.speeda.Core.repository;

import com.speeda.Core.model.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    Optional<Strategy> findBySessionId(Long sessionId);

}
