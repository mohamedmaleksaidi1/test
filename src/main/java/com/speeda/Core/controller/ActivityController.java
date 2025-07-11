package com.speeda.Core.controller;

import com.speeda.Core.dto.ActivityDTO;
import com.speeda.Core.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityDTO> create(@RequestBody ActivityDTO activityDTO) {
        return ResponseEntity.ok(activityService.createActivity(activityDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> update(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        return ResponseEntity.ok(activityService.updateActivity(id, activityDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActivityDTO> partialUpdate(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        return ResponseEntity.ok(activityService.partialUpdateActivity(id, activityDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivity(id));
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAll() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/me")
    public ResponseEntity<ActivityDTO> getByCurrentUser() {
        return ResponseEntity.ok(activityService.getActivityByCurrentUser());
    }
}
