package app.whosin.games.repository;

import app.whosin.games.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findByPublicId(String publicId);
}
