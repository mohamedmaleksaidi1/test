package com.speeda.Core.service;

import com.speeda.Core.dto.ActivityDTO;
import com.speeda.Core.model.Activity;

import java.util.List;

import java.util.List;

public interface IActivityService {
    ActivityDTO createActivity(ActivityDTO dto);
    ActivityDTO updateActivity(Long id, ActivityDTO dto);
    ActivityDTO partialUpdateActivity(Long id, ActivityDTO dto);
    void deleteActivity(Long id);
    ActivityDTO getActivity(Long id);
    List<ActivityDTO> getAllActivities();
    ActivityDTO getActivityByCurrentUser();
}
