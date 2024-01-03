package de.schwarz.libraryapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtIssuer {

    private final JwtProperties properties;

    public String issue(Request request) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(request.userId))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(properties.getTokenDuration()))
                .withClaim("email", request.getEmail())
                .withClaim("roles", request.getRoles())
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    @Getter
    @Builder
    public static class Request {
        private final Long userId;
        private final String email;
        private final List<String> roles;
    }
}
