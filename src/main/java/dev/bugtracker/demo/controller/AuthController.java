package dev.bugtracker.demo.controller;

import dev.bugtracker.demo.dto.AuthenticationResponse;
import dev.bugtracker.demo.dto.LoginRequest;
import dev.bugtracker.demo.dto.RefreshTokenRequest;
import dev.bugtracker.demo.dto.UserPostDto;
import dev.bugtracker.demo.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .status(OK)
                .body(authService.login(loginRequest));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/user/create")
    @ResponseStatus(CREATED)
    public void createNewUserAccount(@RequestBody @Valid UserPostDto userPostDto) {
        authService.createNewAccount(userPostDto);
    }

    @GetMapping("/confirmation/{token}")
    @ResponseStatus(OK)
    public void verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity
                .status(OK)
                .body(authService.refreshToken(refreshTokenRequest));
    }

}
