package app.whosin.gameParticipants.controller;

import app.whosin.gameParticipants.dto.JoinGameRequest;
import app.whosin.gameParticipants.service.GameParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameParticipantController {
    private final GameParticipantService gameParticipantService;

    public GameParticipantController(GameParticipantService gameParticipantService) {
        this.gameParticipantService = gameParticipantService;
    }

    @PostMapping("/{publicId}/join")
    @ResponseStatus(HttpStatus.CREATED)
    public void join(
            @PathVariable String publicId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(
                    value = "X-Anonymous-Token",
                    required = false
            ) String anonymousToken,
            @Valid @RequestBody JoinGameRequest request
    ) {
        if (jwt != null) {
            UUID userId = UUID.fromString(jwt.getSubject());

            gameParticipantService.joinAsUser(
                    publicId,
                    userId
            );

            return;
        }

        if (anonymousToken != null) {
            gameParticipantService.joinAsAnonymous(
                    publicId,
                    anonymousToken,
                    request
            );
            return;
        }

        throw new RuntimeException(
                "Authentication or anonymous identity is required"
        );
    }

}
