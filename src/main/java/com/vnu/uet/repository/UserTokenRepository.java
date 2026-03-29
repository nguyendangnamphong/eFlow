package com.vnu.uet.repository;

import com.vnu.uet.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findOneByTokenStr(String tokenStr);
    void deleteByEmail(String email);
}
