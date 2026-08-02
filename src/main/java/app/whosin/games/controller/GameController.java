package app.whosin.games.controller;

import app.whosin.games.dto.CreateGameRequest;
import app.whosin.games.dto.GameResponse;
import app.whosin.games.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GameResponse> findAll() {
        return gameService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateGameRequest request) {
        UUID organizerId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));

        return gameService.createGame(organizerId, request);
    }
}
