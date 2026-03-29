package com.vnu.uet.service;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import com.vnu.uet.service.dto.ProfileDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AccountManagementService {

    private final UserProfileRepository userProfileRepository;
    private final PermissionManagementService permissionManagementService;

    public AccountManagementService(UserProfileRepository userProfileRepository, 
                                  PermissionManagementService permissionManagementService) {
        this.userProfileRepository = userProfileRepository;
        this.permissionManagementService = permissionManagementService;
    }

    public String createEmployee(ProfileDTO dto) {
        Optional<UserProfile> existing = userProfileRepository.findOneByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống");
        }

        UserProfile profile = new UserProfile();
        profile.setEmail(dto.getEmail());
        profile.setFirstName(dto.getFirstName());
        profile.setPhone(dto.getPhone());
        profile.setDepartment(dto.getDepartment());
        // Simulating random password generation
        String rawPassword = UUID.randomUUID().toString().substring(0, 8) + "@123";
        // Mock encode password
        profile.setPassword("ENCODED_" + rawPassword);
        userProfileRepository.save(profile);
        
        // Assign default perm
        permissionManagementService.assignDefaultPermission(dto.getEmail());

        return rawPassword;
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> searchUserBasicInfo(String email) {
        return userProfileRepository.findOneByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkIfDeletable(String targetEmail, String requesterEmail) {
        try {
            boolean isTargetManager = permissionManagementService.isManager(targetEmail);
            boolean isRequesterLevel4 = permissionManagementService.hasPermission(requesterEmail, 4);
            
            if (isTargetManager && !isRequesterLevel4) {
                return false; // Level 1 cannot delete managers
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteUserAccount(String email) {
        userProfileRepository.findOneByEmail(email).ifPresent(profile -> {
            userProfileRepository.delete(profile);
        });
    }
}
