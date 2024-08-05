package com.app.user.repository;

import com.app.user.model.User;
import com.app.user.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByToken(String token);
    UserToken findByUser(User user);
    void deleteByUser(User user);

    Optional<UserToken> findByUserId(Long userId);

}
