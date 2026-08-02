package app.whosin.games.dto;

import app.whosin.games.entity.GameStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record GameResponse(
        String publicId,
        UUID organizerId,
        String organizerName,
        String title,
        String sport,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String location,
        Integer maxPlayers,
        GameStatus status,
        OffsetDateTime cancelledAt,
        BigDecimal costPerPlayer,
        String notes,
        Instant createdAt,
        Instant updatedAt

) {
}