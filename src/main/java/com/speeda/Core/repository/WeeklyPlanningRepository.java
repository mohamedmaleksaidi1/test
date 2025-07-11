package com.speeda.Core.repository;

import com.speeda.Core.model.WeeklyPlanning;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface WeeklyPlanningRepository extends JpaRepository<WeeklyPlanning, Long> {
    @Query("""
        SELECT wp FROM WeeklyPlanning wp
        JOIN wp.strategy s
        JOIN s.session sess
        JOIN sess.user u
        WHERE wp.id = :id AND u.id = :userId
    """)
    Optional<WeeklyPlanning> findByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT wp FROM WeeklyPlanning wp
        JOIN wp.strategy s
        JOIN s.session sess
        JOIN sess.user u
        WHERE u.id = :userId
    """)
    List<WeeklyPlanning> findAllByUserId(Long userId);
}

