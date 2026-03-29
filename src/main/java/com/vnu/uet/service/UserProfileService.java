package com.vnu.uet.service;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import com.vnu.uet.service.dto.ProfileDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> getUserProfileByEmail(String email) {
        return userProfileRepository.findOneByEmail(email);
    }

    public UserProfile saveProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    public UserProfile updateProfile(String email, ProfileDTO dto) {
        return userProfileRepository.findOneByEmail(email)
            .map(profile -> {
                if (dto.getFirstName() != null) profile.setFirstName(dto.getFirstName());
                if (dto.getPhone() != null) profile.setPhone(dto.getPhone());
                if (dto.getDob() != null) profile.setDob(dto.getDob());
                if (dto.getGender() != null) profile.setGender(dto.getGender());
                if (dto.getPosition() != null) profile.setPosition(dto.getPosition());
                if (dto.getJob() != null) profile.setJob(dto.getJob());
                if (dto.getDepartment() != null) profile.setDepartment(dto.getDepartment());
                if (dto.getAvatar() != null) profile.setAvatar(dto.getAvatar());
                return userProfileRepository.save(profile);
            })
            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
    }
}
