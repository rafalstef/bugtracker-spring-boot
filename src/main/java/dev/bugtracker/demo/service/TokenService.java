package dev.bugtracker.demo.service;

import dev.bugtracker.demo.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private final int jwtExpirationInMillis;

    public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.expiration}") int jwtExpirationInMillis) {
        this.jwtEncoder = jwtEncoder;
        this.jwtExpirationInMillis = jwtExpirationInMillis;
    }

    private String buildToken(User user) {
        Instant now = Instant.now();
        String scope = buildScope(user.getRole().getGrantedAuthorities());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpirationInMillis))
                .claim("scope", scope)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String buildScope(@NotNull Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    public String generateToken(User user) {
        return buildToken(user);
    }

}
