package com.speeda.Core.service;
import com.speeda.Core.dto.PreferenceDTO;
import com.speeda.Core.mapper.PreferenceMapper;
import com.speeda.Core.model.Preference;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.PreferenceRepository;
import com.speeda.Core.repository.UserRepository;
import com.speeda.Core.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService implements IPreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final PreferenceMapper preferenceMapper;
    private final UserContext userContext;

    @Override
    public PreferenceDTO createPreference(PreferenceDTO dto) {
        Long userId = userContext.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Preference preference = preferenceMapper.toEntity(dto);
        preference.setUser(user);
        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    @Override
    public PreferenceDTO updatePreference(Long id, PreferenceDTO dto) {
        Long userId = userContext.getCurrentUserId();
        Preference existing = preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Preference preference = preferenceMapper.toEntity(dto);
        preference.setId(id);
        preference.setUser(user);
        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    @Override
    @Transactional
    public PreferenceDTO partialUpdatePreference(Long id, PreferenceDTO dto) {
        Preference existing = preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        preferenceMapper.updatePreferenceFromDto(dto, existing);
        return preferenceMapper.toDto(preferenceRepository.save(existing));
    }

    @Override
    public void deletePreference(Long id) {
        preferenceRepository.deleteById(id);
    }

    @Override
    public PreferenceDTO getPreference(Long id) {
        return preferenceMapper.toDto(preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found")));
    }

    @Override
    public List<PreferenceDTO> getAllPreferences() {
        return preferenceRepository.findAll().stream()
                .map(preferenceMapper::toDto)
                .toList();
    }

    @Override
    public PreferenceDTO getPreferenceByCurrentUser() {
        Long userId = userContext.getCurrentUserId();
        return preferenceRepository.findByUserId(userId)
                .map(preferenceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Preference not found for this user"));
    }
}
