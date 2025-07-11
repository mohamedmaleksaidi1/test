package com.speeda.Core.repository;

import com.speeda.Core.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUserId(Long userId);
}
