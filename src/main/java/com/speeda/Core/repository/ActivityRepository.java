package com.speeda.Core.repository;

import com.speeda.Core.model.Activity;
import com.speeda.Core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByUserId(Long userId);
    Optional<Activity> findByUser(User user);

}
