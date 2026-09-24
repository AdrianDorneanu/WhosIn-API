package app.whosin.anonymousIdentities.service;

import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import app.whosin.anonymousIdentities.exception.InvalidAnonymousTokenException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class AnonymousIdentityJwtService {
    private final JwtDecoder anonymousTokenDecoder;
    private final JwtEncoder anonymousTokenEncoder;
    private final String issuer;
    private final Duration anonymousTokenDuration;

    public AnonymousIdentityJwtService(
            @Qualifier("anonymousTokenEncoder") JwtEncoder anonymousTokenEncoder,
            @Qualifier("anonymousTokenDecoder") JwtDecoder anonymousTokenDecoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.anonymous-duration}") Duration anonymousTokenDuration
    ) {
        this.anonymousTokenEncoder = anonymousTokenEncoder;
        this.anonymousTokenDecoder = anonymousTokenDecoder;
        this.issuer = issuer;
        this.anonymousTokenDuration = anonymousTokenDuration;
    }

    public String createToken(AnonymousIdentity identity) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(identity.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(anonymousTokenDuration))
                .claim("type", "anonymous")
                .build();

        return anonymousTokenEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }


    public UUID validateToken(String token) {
        try {
            Jwt jwt = anonymousTokenDecoder.decode(token);

            String type = jwt.getClaimAsString("type");

            if (!"anonymous".equals(type)) {
                throw new InvalidAnonymousTokenException();
            }

            return UUID.fromString(
                    Objects.requireNonNull(jwt.getSubject())
            );
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidAnonymousTokenException();
        }
    }
}
