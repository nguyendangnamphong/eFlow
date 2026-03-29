package com.vnu.uet.service;

import com.vnu.uet.domain.UserToken;
import com.vnu.uet.repository.UserTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TokenManagementService {

    private final UserTokenRepository userTokenRepository;

    public TokenManagementService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public String generateToken(String email) {
        // Mocking a JWT for demonstration since JJWT might not be fully configured for custom generation
        String tokenStr = "eyJhbGciOiJIUzI1NiJ9." + UUID.randomUUID().toString() + ".signaturesignature";
        
        UserToken token = new UserToken();
        token.setEmail(email);
        token.setTokenStr(tokenStr);
        token.setCreatedAt(Instant.now());
        token.setExpiryDate(Instant.now().plus(24, ChronoUnit.HOURS));
        token.setIsRevoked(false);
        
        userTokenRepository.save(token);
        return tokenStr;
    }

    public boolean validateToken(String tokenStr, String email) {
        Optional<UserToken> tokenOpt = userTokenRepository.findOneByTokenStr(tokenStr);
        if (tokenOpt.isPresent()) {
            UserToken token = tokenOpt.get();
            if (token.getIsRevoked() == null || token.getIsRevoked()) return false;
            if (token.getExpiryDate().isBefore(Instant.now())) return false;
            // if email is provided, it must match
            if (email != null && !email.equals(token.getEmail())) return false;
            return true;
        }
        return false;
    }
    
    public String getEmailFromToken(String tokenStr) {
        return userTokenRepository.findOneByTokenStr(tokenStr).map(UserToken::getEmail).orElse(null);
    }
    
    public void revokeTokensForUser(String email) {
        userTokenRepository.deleteByEmail(email);
    }
}
