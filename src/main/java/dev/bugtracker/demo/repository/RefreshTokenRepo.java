package dev.bugtracker.demo.repository;

import dev.bugtracker.demo.model.RefreshToken;
import dev.bugtracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.id = ?1, t.token = ?2, t.user = ?3, t.expiryDate = ?4 WHERE t.id = ?1")
    void updateRefreshTokenById(Long id, String token, User user, Instant expiryDate);

    void deleteByToken(String token);
}
