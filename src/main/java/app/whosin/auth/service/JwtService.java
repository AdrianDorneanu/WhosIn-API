package app.whosin.auth.service;

import app.whosin.auth.dto.AuthResponse;
import app.whosin.auth.exception.InvalidRefreshTokenException;
import app.whosin.users.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtEncoder accessTokenEncoder;
    private final JwtEncoder refreshTokenEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final Duration accessTokenDuration;
    private final Duration refreshTokenDuration;

    public JwtService(
            @Qualifier("accessTokenEncoder") JwtEncoder accessTokenEncoder,
            @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder,
            @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
            @Value("${security.jwt.access-duration}") Duration accessTokenDuration,
            @Value("${security.jwt.refresh-duration}") Duration refreshTokenDuration
    ) {
        this.accessTokenEncoder = accessTokenEncoder;
        this.refreshTokenDecoder = refreshTokenDecoder;
        this.refreshTokenEncoder = refreshTokenEncoder;
        this.accessTokenDuration = accessTokenDuration;
        this.refreshTokenDuration = refreshTokenDuration;
    }

    public AuthResponse createTokens(User user) {
        String accessToken = createToken(
                user,
                "access",
                accessTokenDuration,
                accessTokenEncoder
        );

        String refreshToken = createToken(
                user,
                "refresh",
                refreshTokenDuration,
                refreshTokenEncoder
        );

        return new AuthResponse(accessToken, refreshToken);
    }

    public UUID validateRefreshToken(String token) {
        try {
            Jwt jwt = refreshTokenDecoder.decode(token);

            String tokenType = jwt.getClaimAsString("type");

            if (!"refresh".equals(tokenType)) {
                throw new InvalidRefreshTokenException();
            }

            return UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidRefreshTokenException();
        }
    }

    private String createToken(User user, String tokenType, Duration duration, JwtEncoder encoder) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("whosin-api")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(duration))
                .claim("email", user.getEmail())
                .claim("type", tokenType)
                .build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(claims);

        return encoder.encode(parameters).getTokenValue();
    }
}
