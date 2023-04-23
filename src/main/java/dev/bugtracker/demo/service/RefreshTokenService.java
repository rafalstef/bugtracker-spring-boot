package dev.bugtracker.demo.service;

import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.model.RefreshToken;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.RefreshTokenRepo;
import dev.bugtracker.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    @Value("${refresh-token.expiration}")
    private final Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo,
                               UserRepo userRepo,
                               @Value("${refresh-token.expiration}") Long refreshTokenDurationMs) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
        this.refreshTokenDurationMs = refreshTokenDurationMs;
    }

    public RefreshToken generateRefreshToken(String username) {
        // find user
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));

        // create refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(this.refreshTokenDurationMs))
                .build();

        // check if previous refresh token is already in the database
        Optional<RefreshToken> previousToken = refreshTokenRepo.findByUser(user);

        if (previousToken.isPresent()) {
            // update previous token
            refreshTokenRepo.updateRefreshTokenById(
                    previousToken.get().getId(),
                    refreshToken.getToken(),
                    refreshToken.getUser(),
                    refreshToken.getExpiryDate()
            );
            return refreshToken;
        } else {
            // save new token to repository
            return refreshTokenRepo.save(refreshToken);
        }
    }

    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh Token"));
    }

    public void verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.deleteByToken(refreshToken.getToken());
            throw new CredentialsExpiredException("Refresh token was expired. Please make a new sign in request");
        }
    }

    public RefreshToken updateRefreshToken(RefreshToken refreshToken) {
        // create new RefreshToken
        RefreshToken newRefreshToken = new RefreshToken(
                refreshToken.getId(),
                UUID.randomUUID().toString(),
                refreshToken.getUser(),
                Instant.now().plusMillis(this.refreshTokenDurationMs)
        );
        // update token in the database
        refreshTokenRepo.updateRefreshTokenById(
                newRefreshToken.getId(),
                newRefreshToken.getToken(),
                newRefreshToken.getUser(),
                newRefreshToken.getExpiryDate()
        );
        return newRefreshToken;
    }
}