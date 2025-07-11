package com.speeda.Core.controller;

import com.speeda.Core.dto.ActivityDTO;
import com.speeda.Core.dto.PreferenceDTO;
import com.speeda.Core.mapper.ActivityMapper;
import com.speeda.Core.mapper.PreferenceMapper;
import com.speeda.Core.model.Activity;
import com.speeda.Core.model.Preference;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.ActivityRepository;
import com.speeda.Core.repository.PreferenceRepository;
import com.speeda.Core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicWebhookController {

    private final UserRepository userRepository;
    private final PreferenceRepository preferenceRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    @Autowired
    private PreferenceMapper preferenceMapper;

    @PostMapping("/preferences")
    public ResponseEntity<?> addPreference(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestBody PreferenceDTO dto) {

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable"));
        }

        Preference preference = preferenceMapper.toEntity(dto);
        preference.setUser(userOpt.get());
        Preference saved = preferenceRepository.save(preference);

        return ResponseEntity.ok(preferenceMapper.toDto(saved));
    }


    @GetMapping("/activity")
    public ResponseEntity<?> getActivityByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable"));
        }

        Optional<Activity> activityOpt = activityRepository.findByUser(userOpt.get());
        if (activityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Aucune activité trouvée pour ce numéro"));
        }

        ActivityDTO dto = activityMapper.toDto(activityOpt.get());
        return ResponseEntity.ok(dto);
    }
}
