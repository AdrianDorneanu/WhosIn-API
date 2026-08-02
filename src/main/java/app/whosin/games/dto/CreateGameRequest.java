package app.whosin.games.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateGameRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must contain at most 255 characters")
        String title,

        @NotBlank(message = "Sport is required")
        @Size(max = 255, message = "Sport must contain at most 255 characters")
        String sport,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        OffsetDateTime startsAt,

        @NotNull(message = "End time is required")
        OffsetDateTime endsAt,

        @NotBlank(message = "Location is required")
        @Size(max = 255, message = "Location must contain at most 255 characters")
        String location,

        @NotNull(message = "Maximum number of players is required")
        @Min(value = 1, message = "Maximum number of players must be at least 1")
        Integer maxPlayers,

        @DecimalMin(
                value = "0.00",
                message = "Cost per player cannot be negative"
        )
        BigDecimal costPerPlayer,

        @Size(max = 500, message = "Notes must contain at most 500 characters")
        String notes
) {
}