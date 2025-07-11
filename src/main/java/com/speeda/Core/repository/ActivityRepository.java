package com.speeda.Core.repository;

import com.speeda.Core.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByUserId(Long userId);
}
