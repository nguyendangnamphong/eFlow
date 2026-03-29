package com.vnu.uet.service;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthInterService {

    private final UserProfileRepository userProfileRepository;

    public AuthInterService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean verifyEmailAndPassword(String email, String rawPassword) {
        if (email == null || rawPassword == null) return false;
        Optional<UserProfile> profile = userProfileRepository.findOneByEmail(email);
        if (profile.isPresent()) {
            // Mocking BCrypt check
            String encodedPass = profile.orElseThrow().getPassword();
            return encodedPass != null && encodedPass.contains(rawPassword);
        }
        return false;
    }
}
