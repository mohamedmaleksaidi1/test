package com.speeda.Core.controller;

import com.speeda.Core.dto.StrategyDTO;
import com.speeda.Core.service.StrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<StrategyDTO> getBySessionId(@PathVariable Long sessionId) {
        return ResponseEntity.ok(strategyService.getStrategyBySessionId(sessionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrategyDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(strategyService.getStrategy(id));
    }

    @GetMapping
    public ResponseEntity<List<StrategyDTO>> getAll() {
        return ResponseEntity.ok(strategyService.getAllStrategies());
    }
}
