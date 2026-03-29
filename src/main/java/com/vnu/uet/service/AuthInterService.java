package com.vnu.uet.service;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            String encodedPass = profile.get().getPassword();
            return encodedPass != null && encodedPass.contains(rawPassword);
        }
        return false;
    }
}
