package com.quantnexus.service;

import com.quantnexus.domain.User;
import com.quantnexus.domain.enums.Role;
import com.quantnexus.domain.enums.UserStatus;
import com.quantnexus.dto.auth.UserProfileResponse;
import com.quantnexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToProfileResponse);
    }

    @Transactional
    public UserProfileResponse updateUserRole(Long targetUserId, Role newRole, Long currentAdminId) {
        // EDGE CASE FIX: Prevent Self-Demotion
        if (targetUserId.equals(currentAdminId)) {
            throw new IllegalArgumentException("Security Block: You cannot modify your own role.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUserId));

        user.setRole(newRole);
        log.info("Admin [{}] updated User [{}] role to [{}]", currentAdminId, user.getEmail(), newRole);

        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse toggleUserStatus(Long targetUserId, Long currentAdminId) {
        // EDGE CASE FIX: Prevent Self-Lockout
        if (targetUserId.equals(currentAdminId)) {
            throw new IllegalArgumentException("Security Block: You cannot lock/unlock your own account.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + targetUserId));

        if (user.getUserStatus() == UserStatus.ACTIVE) {
            user.setUserStatus(UserStatus.INACTIVE);
        } else {
            user.setUserStatus(UserStatus.ACTIVE);
            user.setFailedLoginAttempts(0);
        }

        log.info("Admin [{}] toggled User [{}] status to [{}]", currentAdminId, user.getEmail(), user.getUserStatus());
        return mapToProfileResponse(user);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(), user.getEmail(), user.getFullName(),
                user.getRole().name(), user.getUserStatus().name(),
                user.getBaseCurrency().name(), user.getLastLoginAt(),
                user.getFailedLoginAttempts(), user.getCreatedAt()
        );
    }
}