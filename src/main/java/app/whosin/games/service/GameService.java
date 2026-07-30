package app.whosin.games.service;

import app.whosin.games.dto.GameResponse;
import app.whosin.games.mapper.GameMapper;
import app.whosin.games.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
public class GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    @Transactional()
    public List<GameResponse> findAll() {
        return gameRepository.findAll().stream().map(gameMapper::toResponse).toList();
    }
}
