package app.whosin.auth.service;

import app.whosin.auth.dto.*;
import app.whosin.auth.exception.EmailAlreadyExistsException;
import app.whosin.auth.exception.InvalidCredentialsException;
import app.whosin.auth.exception.InvalidRefreshTokenException;
import app.whosin.users.entity.User;
import app.whosin.users.entity.UserStatus;
import app.whosin.users.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public SignupResponse signUp(SignupRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        boolean emailAlreadyExists = userRepository.existsByEmail(normalizedEmail);

        if (emailAlreadyExists) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(normalizedEmail,
                passwordHash,
                request.displayName(),
                null,
                "Europe/Amsterdam");

        User savedUser = userRepository.save(user);

        return toSignupResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(this::invalidCredentials);

        if (user.getPasswordHash() == null) {
            throw invalidCredentials();
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw invalidCredentials();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw invalidCredentials();
        }

        return jwtService.createTokens(user);
    }

    @Transactional(readOnly = true)
    public SignupResponse getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Authenticated user no longer exists"
                        )
                );

        return toSignupResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest request) {
        UUID userId = jwtService.validateRefreshToken(
                request.refreshToken()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidRefreshTokenException();
        }

        return jwtService.createTokens(user);
    }

    private InvalidCredentialsException invalidCredentials() {
        return new InvalidCredentialsException(
                "Invalid email or password"
        );
    }

    private SignupResponse toSignupResponse(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
