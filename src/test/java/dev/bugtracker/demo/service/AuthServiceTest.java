package dev.bugtracker.demo.service;

import dev.bugtracker.demo.email.EmailDetails;
import dev.bugtracker.demo.email.EmailService;
import dev.bugtracker.demo.enumeration.UserRole;
import dev.bugtracker.demo.exception.ResourceNotFoundException;
import dev.bugtracker.demo.dto.AuthenticationResponse;
import dev.bugtracker.demo.dto.LoginRequest;
import dev.bugtracker.demo.dto.RefreshTokenRequest;
import dev.bugtracker.demo.dto.UserPostDto;
import dev.bugtracker.demo.model.ConfirmationToken;
import dev.bugtracker.demo.model.RefreshToken;
import dev.bugtracker.demo.model.SecurityUser;
import dev.bugtracker.demo.model.User;
import dev.bugtracker.demo.repository.ConfirmationTokenRepo;
import dev.bugtracker.demo.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Captor
    ArgumentCaptor<ConfirmationToken> confirmationTokenCaptor;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;
    @Captor
    ArgumentCaptor<EmailDetails> emailCaptor;
    @Captor
    ArgumentCaptor<String> usernameArgumentCapture;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Mock
    private EmailService emailService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthService sut;

    @Test
    void getCurrentUser() {
        String email = "some_email";

        User user = User.builder()
                .email(email)
                .build();

        Jwt jwt = Mockito.mock(Jwt.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        User currentUser = sut.getCurrentUser();

        verify(userRepo).findByEmail(email);
        assertEquals(user, currentUser);
    }

    @Test
    void createNewAccount() {
        String email = "john.doe@email.com";
        String password = "password";

        UserPostDto registerRequest = UserPostDto.builder()
                .email(email)
                .password(password)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1994, 2, 23))
                .role(UserRole.DEVELOPER)
                .build();

        when(passwordEncoder.encode(password)).thenReturn(password);

        sut.createNewAccount(registerRequest);

        verify(userRepo).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(email, capturedUser.getEmail());
        assertEquals(password, capturedUser.getPassword());

        verify(confirmationTokenRepo).save(confirmationTokenCaptor.capture());
        ConfirmationToken capturedConfirmationToken = confirmationTokenCaptor.getValue();
        assertEquals(capturedUser, capturedConfirmationToken.getUser());

        assertDoesNotThrow(() -> verify(emailService).sendSimpleMail(emailCaptor.capture()));
        EmailDetails capturedEmail = emailCaptor.getValue();
        assertEquals(email, capturedEmail.getRecipient());
    }

    @Test
    void login() {
        String email = "john.doe@email.com";
        String password = "password";
        String accessToken = "access_token";

        User user = User.builder()
                .email(email)
                .password(password)
                .build();

        SecurityUser securityUser = new SecurityUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token("token")
                .build();

        LoginRequest loginRequest = new LoginRequest(email, password);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(tokenService.generateToken(user)).thenReturn(accessToken);
        when(refreshTokenService.generateRefreshToken(email)).thenReturn(refreshToken);


        AuthenticationResponse authenticationResponse = sut.login(loginRequest);

        verify(tokenService).generateToken(userArgumentCaptor.capture());
        assertEquals(user, userArgumentCaptor.getValue());

        verify(refreshTokenService).generateRefreshToken(usernameArgumentCapture.capture());
        assertEquals(email, usernameArgumentCapture.getValue());

        assertEquals(accessToken, authenticationResponse.getAccessToken());
        assertEquals(refreshToken.getToken(), authenticationResponse.getRefreshToken());
    }

    @Test
    void verifyAccount_ValidToken_EnablesUser() {
        String email = "john.doe@email.com";
        User user = User.builder()
                .id(123L)
                .email(email)
                .password("password")
                .build();

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .user(user)
                .build();

        when(confirmationTokenRepo.findByToken(token)).thenReturn(Optional.of(confirmationToken));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        sut.verifyAccount(token);

        verify(userRepo).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertTrue(capturedUser.getIsEnabled());
    }

    @Test
    void verifyAccount_InvalidToken_ThrowsError() {
        String token = UUID.randomUUID().toString();
        when(confirmationTokenRepo.findByToken(token)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> sut.verifyAccount(token)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void refreshToken_ValidToken_Return() {
        User user = User.builder()
                .id(123L)
                .email("jonh.doe@email.com")
                .password("password")
                .build();

        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .id(10L)
                .token(token)
                .user(user)
                .build();

        String updatedToken = UUID.randomUUID().toString();
        RefreshToken updatedRefreshToken = RefreshToken.builder()
                .id(10L)
                .token(updatedToken)
                .user(user)
                .build();

        when(refreshTokenService.getRefreshToken(token)).thenReturn(refreshToken);
        when(refreshTokenService.updateRefreshToken(refreshToken)).thenReturn(updatedRefreshToken);
        String accessToken = UUID.randomUUID().toString();
        when(tokenService.generateToken(user)).thenReturn(accessToken);

        AuthenticationResponse authResponse = sut.refreshToken(new RefreshTokenRequest(token));

        verify(refreshTokenService).getRefreshToken(token);
        verify(refreshTokenService).verifyExpiration(refreshToken);
        verify(refreshTokenService).updateRefreshToken(refreshToken);
        verify(tokenService).generateToken(user);

        assertEquals(accessToken, authResponse.getAccessToken());
        assertEquals(updatedToken, authResponse.getRefreshToken());
    }
}