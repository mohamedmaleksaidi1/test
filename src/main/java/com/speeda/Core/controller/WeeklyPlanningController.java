package com.speeda.Core.controller;

import com.speeda.Core.dto.WeeklyPlanningDTO;
import com.speeda.Core.service.WeeklyPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weeklyplannings")
@RequiredArgsConstructor
public class WeeklyPlanningController {

    private final WeeklyPlanningService weeklyPlanningService;

    @GetMapping("/{id}")
    public ResponseEntity<WeeklyPlanningDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(weeklyPlanningService.getWeeklyPlanning(id));
    }

    @GetMapping
    public ResponseEntity<List<WeeklyPlanningDTO>> getAll() {
        return ResponseEntity.ok(weeklyPlanningService.getAllWeeklyPlannings());
    }
}
