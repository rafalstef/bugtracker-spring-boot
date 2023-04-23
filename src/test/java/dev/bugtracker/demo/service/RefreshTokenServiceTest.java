package dev.bugtracker.demo.service;

import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.model.RefreshToken;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.RefreshTokenRepo;
import dev.bugtracker.demo.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Captor
    ArgumentCaptor<Long> idCaptor;
    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<String> tokenCaptor;
    @Captor
    ArgumentCaptor<Instant> expiryCaptor;
    @Mock
    private RefreshTokenRepo refreshTokenRepo;
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private RefreshTokenService sut;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(sut, "refreshTokenDurationMs", 10000L);
    }

    @Test
    void generateRefreshToken_PreviousTokenNotExists_ReturnsRefreshToken() {
        String username = "john";
        User user = User.builder().email(username).build();

        when(userRepo.findByEmail(username)).thenReturn(Optional.of(user));
        when(refreshTokenRepo.findByUser(user)).thenReturn(Optional.empty());
        sut.generateRefreshToken(username);

        ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).save(refreshTokenArgumentCaptor.capture());

        assertEquals(user, refreshTokenArgumentCaptor.getValue().getUser());
    }

    @Test
    void generateRefreshToken_PreviousTokenExists_UpdatesRefreshToken() {
        String username = "john";
        User user = User.builder().email(username).build();

        Long refreshId = 30L;
        String token = "some_token";
        RefreshToken refreshToken = RefreshToken.builder()
                .id(refreshId)
                .user(user)
                .token(token)
                .expiryDate(Instant.now().minusMillis(1000))
                .build();

        when(userRepo.findByEmail(username)).thenReturn(Optional.of(user));
        when(refreshTokenRepo.findByUser(user)).thenReturn(Optional.of(refreshToken));
        sut.generateRefreshToken(username);

        verify(refreshTokenRepo).updateRefreshTokenById(idCaptor.capture(), tokenCaptor.capture(),
                userCaptor.capture(), expiryCaptor.capture());

        assertEquals(refreshId, idCaptor.getValue());
        assertNotEquals(token, tokenCaptor.getValue());
        assertEquals(user, userCaptor.getValue());
        assertTrue(Instant.now().isBefore(expiryCaptor.getValue()));

        verify(refreshTokenRepo, never()).save(Mockito.any());
    }

    @Test
    void getRefreshToken_TokenExists_ReturnsRefresh() {
        String token = "some_token";
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .build();

        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));
        RefreshToken rt = sut.getRefreshToken(token);

        verify(refreshTokenRepo).findByToken(token);
        assertEquals(refreshToken, rt);
    }

    @Test
    void getRefreshToken_TokenDoesNotExists_ThrowsResourceNotFoundException() {
        String token = "some_token";
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> sut.getRefreshToken(token));
    }

    @Test
    void verifyExpiration_ExpiredToken_DeletesTokenAndThrowsException() {
        RefreshToken expiredRefresh = RefreshToken.builder()
                .id(12L)
                .token("token")
                .expiryDate(Instant.now().minusMillis(100))
                .build();

        assertThrows(CredentialsExpiredException.class, () -> {
            sut.verifyExpiration(expiredRefresh);
            verify(refreshTokenRepo).deleteByToken(expiredRefresh.getToken());
        });
    }

    @Test
    void verifyExpiration_ValidToken_DoesNotThrowException() {
        RefreshToken expiredRefresh = RefreshToken.builder()
                .id(12L)
                .token("token")
                .expiryDate(Instant.now().plusMillis(100))
                .build();

        assertDoesNotThrow(() -> sut.verifyExpiration(expiredRefresh));
    }

    @Test
    void updateRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(12L)
                .user(User.builder().firstName("John").build())
                .token("token")
                .expiryDate(Instant.now().minusMillis(100))
                .build();

        RefreshToken newToken = sut.updateRefreshToken(refreshToken);

        assertEquals(refreshToken.getId(), newToken.getId());
        assertNotEquals(refreshToken.getToken(), newToken.getToken());
        assertEquals(refreshToken.getUser(), newToken.getUser());
        assertTrue(refreshToken.getExpiryDate().isBefore(newToken.getExpiryDate()));
    }
}