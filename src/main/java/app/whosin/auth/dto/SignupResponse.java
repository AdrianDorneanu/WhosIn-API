package app.whosin.auth.dto;

import app.whosin.users.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record SignupResponse(
        UUID id,
        String email,
        String displayName,
        UserStatus status,
        Instant createdAt
) {
}