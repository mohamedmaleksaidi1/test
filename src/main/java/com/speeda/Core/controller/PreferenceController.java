package com.speeda.Core.controller;

import com.speeda.Core.dto.PreferenceDTO;
import com.speeda.Core.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @PostMapping
    public ResponseEntity<PreferenceDTO> create(@RequestBody PreferenceDTO dto) {
        return ResponseEntity.ok(preferenceService.createPreference(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDTO> update(@PathVariable Long id, @RequestBody PreferenceDTO dto) {
        return ResponseEntity.ok(preferenceService.updatePreference(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PreferenceDTO> partialUpdate(@PathVariable Long id, @RequestBody PreferenceDTO dto) {
        return ResponseEntity.ok(preferenceService.partialUpdatePreference(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenceDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(preferenceService.getPreference(id));
    }

    @GetMapping
    public ResponseEntity<List<PreferenceDTO>> getAll() {
        return ResponseEntity.ok(preferenceService.getAllPreferences());
    }

    @GetMapping("/me")
    public ResponseEntity<PreferenceDTO> getByCurrentUser() {
        return ResponseEntity.ok(preferenceService.getPreferenceByCurrentUser());
    }



}
