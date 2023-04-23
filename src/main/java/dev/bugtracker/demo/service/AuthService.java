package dev.bugtracker.demo.service;

import dev.bugtracker.demo.email.EmailDetails;
import dev.bugtracker.demo.email.EmailService;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.SendFailedException;
import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();

        String username = principal.getSubject();
        return userRepo.findByEmail(username).orElseThrow(
                () -> new ResourceNotFoundException("User " + username + " not found in the database."));
    }

    public void createNewAccount(@Valid UserPostDto userPostDto) {
        User user = User.builder()
                .email(userPostDto.getEmail())
                .password(passwordEncoder.encode(userPostDto.getPassword()))
                .firstName(userPostDto.getFirstName())
                .lastName(userPostDto.getLastName())
                .birthDate(userPostDto.getBirthDate())
                .role(userPostDto.getRole())
                .registeredAt(LocalDate.now())
                .build();

        userRepo.save(user);

        // create confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        // save to database
        confirmationTokenRepo.save(confirmationToken);

        // send conformation mail with token
        try {
            EmailDetails email = new EmailDetails(
                    user.getEmail(),
                    "Hello " + user.getFirstName() + "!\n" +
                            "Please click on the below url to activate your account : " +
                            "http://localhost:8080/api/auth/confirmation/" + token,
                    "Verify your email address"
            );
            emailService.sendSimpleMail(email);
        } catch (SendFailedException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user with username {}", loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        String accessToken = tokenService.generateToken(securityUser.getUser());
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(securityUser.getUsername());

        return new AuthenticationResponse(accessToken, refreshToken.getToken());
    }

    public void verifyAccount(String token) {
        // find confirmation token
        ConfirmationToken confirmationToken =
                confirmationTokenRepo.findByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Token: " + token + " not found."));

        String email = confirmationToken.getUser().getEmail();
        // find user
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found."));

        // if found set enabled
        user.setIsEnabled(Boolean.TRUE);
        userRepo.save(user);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // find refresh token in the database
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenRequest.getRefreshToken());
        // check if not expired
        refreshTokenService.verifyExpiration(refreshToken);
        // update refresh token
        String newRefreshToken = refreshTokenService.updateRefreshToken(refreshToken).getToken();
        // generate new access token
        String accessToken = tokenService.generateToken(refreshToken.getUser());

        return new AuthenticationResponse(accessToken, newRefreshToken);
    }
}




