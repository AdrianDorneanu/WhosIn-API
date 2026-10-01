package app.whosin.gameParticipants.service;

import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import app.whosin.anonymousIdentities.exception.InvalidAnonymousTokenException;
import app.whosin.anonymousIdentities.repository.AnonymousIdentityRepository;
import app.whosin.anonymousIdentities.service.AnonymousIdentityJwtService;
import app.whosin.gameParticipants.dto.JoinGameRequest;
import app.whosin.gameParticipants.entity.GameParticipant;
import app.whosin.gameParticipants.repository.GameParticipantRepository;
import app.whosin.games.entity.Game;
import app.whosin.games.repository.GameRepository;
import app.whosin.users.entity.User;
import app.whosin.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GameParticipantService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final AnonymousIdentityRepository anonymousIdentityRepository;
    private final AnonymousIdentityJwtService anonymousIdentityJwtService;

    public GameParticipantService(UserRepository userRepository, GameRepository gameRepository, GameParticipantRepository gameParticipantRepository, AnonymousIdentityRepository anonymousIdentityRepository, AnonymousIdentityJwtService anonymousIdentityJwtService) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameParticipantRepository = gameParticipantRepository;
        this.anonymousIdentityRepository = anonymousIdentityRepository;
        this.anonymousIdentityJwtService = anonymousIdentityJwtService;
    }

    @Transactional
    public void joinAsUser(String gamePublicId, UUID userId) {
        Game game = gameRepository.findByPublicId(gamePublicId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyJoined = gameParticipantRepository.existsByGameIdAndUserId(game.getId(), user.getId());

        if (alreadyJoined) {
            throw new RuntimeException("User already joined the game");
        }

        GameParticipant gameParticipant = GameParticipant.createForUser(game, user, user.getDisplayName());

        gameParticipantRepository.save(gameParticipant);
    }

    @Transactional
    public void joinAsAnonymous(
            String gamePublicId,
            String anonymousToken,
            JoinGameRequest request
    ) {
        Game game = gameRepository.findByPublicId(gamePublicId)
                .orElseThrow(() ->
                        new RuntimeException("Game not found")
                );

        UUID anonymousIdentityId =
                anonymousIdentityJwtService.validateToken(
                        anonymousToken
                );

        AnonymousIdentity anonymousIdentity =
                anonymousIdentityRepository
                        .findById(anonymousIdentityId)
                        .orElseThrow(
                                () -> new InvalidAnonymousTokenException()
                        );

        if (anonymousIdentity.isClaimed()) {
            throw new InvalidAnonymousTokenException();
        }

        boolean alreadyJoined =
                gameParticipantRepository
                        .existsByGameIdAndAnonymousIdentityId(
                                game.getId(),
                                anonymousIdentityId
                        );

        if (alreadyJoined) {
            throw new RuntimeException(
                    "Anonymous participant already joined this game"
            );
        }

        GameParticipant participant =
                GameParticipant.createForAnonymous(
                        game,
                        anonymousIdentity,
                        request.displayName().trim()
                );

        gameParticipantRepository.save(participant);
    }
}
