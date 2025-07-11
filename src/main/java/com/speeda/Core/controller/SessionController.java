package com.speeda.Core.controller;

import com.speeda.Core.dto.SessionDTO;
import com.speeda.Core.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDTO> create(@RequestBody SessionDTO sessionDTO) {
        return ResponseEntity.ok(sessionService.createSession(sessionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDTO> update(@PathVariable Long id, @RequestBody SessionDTO sessionDTO) {
        return ResponseEntity.ok(sessionService.updateSession(id, sessionDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SessionDTO> partialUpdate(@PathVariable Long id, @RequestBody SessionDTO sessionDTO) {
        return ResponseEntity.ok(sessionService.partialUpdateSession(id, sessionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSession(id));
    }

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAll() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/me")
    public ResponseEntity<List<SessionDTO>> getByCurrentUser() {
        return ResponseEntity.ok(sessionService.getSessionsByCurrentUser());
    }
}
