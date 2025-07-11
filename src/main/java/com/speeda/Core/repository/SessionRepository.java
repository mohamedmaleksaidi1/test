package com.speeda.Core.repository;

import com.speeda.Core.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserId(Long userId);

}
