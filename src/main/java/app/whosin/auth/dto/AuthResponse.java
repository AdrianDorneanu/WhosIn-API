package app.whosin.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
