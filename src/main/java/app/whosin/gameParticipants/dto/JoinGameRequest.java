package app.whosin.gameParticipants.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinGameRequest(
        @NotBlank(message = "Display name is required")
        @Size(
                max = 255,
                message = "Display name must contain at most 255 characters"
        )
        String displayName
) {
}