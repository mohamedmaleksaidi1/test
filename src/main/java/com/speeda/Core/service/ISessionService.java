package com.speeda.Core.service;

import com.speeda.Core.dto.SessionDTO;
import com.speeda.Core.model.Session;

import java.util.List;

import java.util.List;

public interface ISessionService {
    SessionDTO createSession(SessionDTO dto);
    SessionDTO updateSession(Long id, SessionDTO dto);
    SessionDTO partialUpdateSession(Long id, SessionDTO dto);
    void deleteSession(Long id);
    SessionDTO getSession(Long id);
    List<SessionDTO> getAllSessions();
    List<SessionDTO> getSessionsByCurrentUser();
}
