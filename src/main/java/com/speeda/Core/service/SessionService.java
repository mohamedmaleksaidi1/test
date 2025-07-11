package com.speeda.Core.service;

import com.speeda.Core.dto.SessionDTO;
import com.speeda.Core.mapper.SessionMapper;
import com.speeda.Core.model.Session;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.SessionRepository;
import com.speeda.Core.repository.UserRepository;
import com.speeda.Core.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService implements ISessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionMapper sessionMapper;
    private final UserContext userContext;

    @Override
    public SessionDTO createSession(SessionDTO dto) {
        Long userId = userContext.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Session session = sessionMapper.toEntity(dto);
        session.setUser(user);
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    @Override
    public SessionDTO updateSession(Long id, SessionDTO dto) {
        Long userId = userContext.getCurrentUserId();
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Session session = sessionMapper.toEntity(dto);
        session.setId(id);
        session.setUser(user);
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public SessionDTO partialUpdateSession(Long id, SessionDTO dto) {
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        sessionMapper.updateSessionFromDto(dto, existing);
        return sessionMapper.toDto(sessionRepository.save(existing));
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    public SessionDTO getSession(Long id) {
        return sessionMapper.toDto(sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found")));
    }

    @Override
    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toDto)
                .toList();
    }

    @Override
    public List<SessionDTO> getSessionsByCurrentUser() {
        Long userId = userContext.getCurrentUserId();
        return sessionRepository.findByUserId(userId).stream()
                .map(sessionMapper::toDto)
                .toList();
    }
}
