package com.quantnexus.controller;

import com.quantnexus.domain.enums.Role;
import com.quantnexus.dto.auth.UserProfileResponse;
import com.quantnexus.security.SecurityUser;
import com.quantnexus.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>>getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable){
        return ResponseEntity.ok(userManagementService.getAllUsers(pageable));
    }

    public ResponseEntity<UserProfileResponse>changeUserRole(
            @AuthenticationPrincipal SecurityUser admin,// Inject the Admin making the request
            @PathVariable Long userId,
            @PathVariable Role role){
        log.warn("API Request: Admin [{}] modifying role for User [{}]", admin.getId(), userId);
        return ResponseEntity.ok(userManagementService.updateUserRole(userId, role, admin.getId()));
    }

    public ResponseEntity<UserProfileResponse>toggleUserStatus(
            @AuthenticationPrincipal SecurityUser admin, // Inject the Admin making the request
            @PathVariable Long userId){
        log.warn("API Request: Admin [{}] toggling status for User [{}]", admin.getId(), userId);
        return ResponseEntity.ok(userManagementService.toggleUserStatus(userId, admin.getId()));
    }
}
