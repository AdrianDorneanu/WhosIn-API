package app.whosin.common.config;

import app.whosin.common.security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private static final String JWT_ISSUER = "whosin-api";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public JwtEncoder accessTokenEncoder(
            @Value("${security.jwt.access-secret}") String secret
    ) {
        SecretKey secretKey = createSecretKey(secret);

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtEncoder refreshTokenEncoder(
            @Value("${security.jwt.refresh-secret}") String secret
    ) {
        SecretKey secretKey = createSecretKey(secret);

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder accessTokenDecoder(
            @Value("${security.jwt.access-secret}") String secret
    ) {
        NimbusJwtDecoder decoder = createDecoder(secret);

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(JWT_ISSUER)
        );

        return decoder;
    }

    @Bean
    public JwtDecoder refreshTokenDecoder(
            @Value("${security.jwt.refresh-secret}") String secret
    ) {
        NimbusJwtDecoder decoder = createDecoder(secret);

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(JWT_ISSUER)
        );

        return decoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("accessTokenDecoder")
            JwtDecoder accessTokenDecoder,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(
                                jwtAuthenticationEntryPoint
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/signup",
                                "/auth/login",
                                "/auth/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(resourceServer ->
                        resourceServer.authenticationEntryPoint(
                                jwtAuthenticationEntryPoint
                        ).jwt(jwt ->
                                jwt.decoder(accessTokenDecoder)
                        )
                );

        return http.build();
    }

    private NimbusJwtDecoder createDecoder(String secret) {
        SecretKey secretKey = createSecretKey(secret);

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    private SecretKey createSecretKey(String secret) {
        return new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }
}