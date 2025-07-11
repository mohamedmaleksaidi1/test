package com.speeda.Core.service;
import com.speeda.Core.dto.*;
import com.speeda.Core.mapper.ActivityMapper;
import com.speeda.Core.model.Activity;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.ActivityRepository;
import com.speeda.Core.repository.UserRepository;
import com.speeda.Core.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService implements IActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ActivityMapper activityMapper;
    private final UserContext userContext;

    @Override
    public ActivityDTO createActivity(ActivityDTO dto) {
        Long userId = userContext.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Activity activity = activityMapper.toEntity(dto);
        activity.setUser(user);
        return activityMapper.toDto(activityRepository.save(activity));
    }

    @Override
    public ActivityDTO updateActivity(Long id, ActivityDTO dto) {
        Long userId = userContext.getCurrentUserId();
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Activity activity = activityMapper.toEntity(dto);
        activity.setId(id);
        activity.setUser(user);
        return activityMapper.toDto(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public ActivityDTO partialUpdateActivity(Long id, ActivityDTO dto) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        activityMapper.updateActivityFromDto(dto, existing);
        return activityMapper.toDto(activityRepository.save(existing));
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public ActivityDTO getActivity(Long id) {
        return activityMapper.toDto(activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found")));
    }

    @Override
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(activityMapper::toDto)
                .toList();
    }

    @Override
    public ActivityDTO getActivityByCurrentUser() {
        Long userId = userContext.getCurrentUserId();
        return activityRepository.findByUserId(userId)
                .map(activityMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Activity not found for this user"));
    }
}
