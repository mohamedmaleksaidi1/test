package com.speeda.Core.service;

import com.speeda.Core.dto.PreferenceDTO;

import java.util.List;

public interface IPreferenceService {
    PreferenceDTO createPreference(PreferenceDTO dto);
    PreferenceDTO updatePreference(Long id, PreferenceDTO dto);
    PreferenceDTO partialUpdatePreference(Long id, PreferenceDTO dto);
    void deletePreference(Long id);
    PreferenceDTO getPreference(Long id);
    List<PreferenceDTO> getAllPreferences();
    PreferenceDTO getPreferenceByCurrentUser();
}
