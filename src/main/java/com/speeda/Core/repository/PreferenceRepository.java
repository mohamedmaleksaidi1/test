package com.speeda.Core.repository;

import com.speeda.Core.model.Preference;
import com.speeda.Core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUserId(Long userId);
    Optional<Preference> findByUser(User user);

}
