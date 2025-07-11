package com.speeda.Core.security;

import com.speeda.Core.model.User;
import com.speeda.Core.repository.UserRepository;
import org.springframework.stereotype.Component;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class UserContext {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }
}
