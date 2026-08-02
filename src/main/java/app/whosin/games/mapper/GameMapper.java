package app.whosin.games.mapper;

import app.whosin.games.dto.GameResponse;
import app.whosin.games.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {
    public GameResponse toResponse(Game game) {
        return new GameResponse(
                game.getPublicId(),
                game.getOrganizer().getId(),
                game.getOrganizer().getDisplayName(),
                game.getTitle(),
                game.getSport(),
                game.getStartsAt(),
                game.getEndsAt(),
                game.getLocation(),
                game.getMaxPlayers(),
                game.getStatus(),
                game.getCancelledAt(),
                game.getCostPerPlayer(),
                game.getNotes(),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }
}