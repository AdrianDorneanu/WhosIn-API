package app.whosin.games.service;

import app.whosin.games.dto.CreateGameRequest;
import app.whosin.games.dto.GameResponse;
import app.whosin.games.entity.Game;
import app.whosin.games.mapper.GameMapper;
import app.whosin.games.repository.GameRepository;
import app.whosin.users.entity.User;
import app.whosin.users.entity.UserStatus;
import app.whosin.users.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, UserRepository userRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameMapper = gameMapper;
    }

    @Transactional(readOnly = true)
    public List<GameResponse> findAll() {
        return gameRepository.findAll().stream().map(gameMapper::toResponse).toList();
    }

    @Transactional
    public GameResponse createGame(UUID organizerId, CreateGameRequest request) {
        User organizer = userRepository.findById(organizerId).orElseThrow(() -> new AccessDeniedException("Authenticated user no longer exists"));

        if (organizer.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException("Only active users can create games");
        }

        String publicId = generatePublicId();

        Game game = new Game(publicId, organizer, request.title(), request.sport(), request.startsAt(), request.endsAt(), request.location(), request.maxPlayers(), request.costPerPlayer(), request.notes());

        Game savedGame = gameRepository.save(game);

        return gameMapper.toResponse(savedGame);
    }

    private String generatePublicId() {
        return UUID.randomUUID().toString();
    }
}
