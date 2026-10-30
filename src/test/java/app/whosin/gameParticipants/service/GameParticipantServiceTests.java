package app.whosin.gameParticipants.service;

import app.whosin.anonymousIdentities.repository.AnonymousIdentityRepository;
import app.whosin.anonymousIdentities.service.AnonymousIdentityJwtService;
import app.whosin.common.exception.ResourceNotFoundException;
import app.whosin.gameParticipants.repository.GameParticipantRepository;
import app.whosin.games.entity.Game;
import app.whosin.games.repository.GameRepository;
import app.whosin.users.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GameParticipantServiceTests {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameRepository gameRepository = mock(GameRepository.class);
    private final GameParticipantRepository participantRepository = mock(GameParticipantRepository.class);
    private final AnonymousIdentityRepository identityRepository = mock(AnonymousIdentityRepository.class);
    private final AnonymousIdentityJwtService jwtService = mock(AnonymousIdentityJwtService.class);
    private final GameParticipantService service = new GameParticipantService(
            userRepository, gameRepository, participantRepository, identityRepository, jwtService
    );

    @Test
    void joiningMissingGameAsUserThrowsResourceNotFound() {
        UUID publicId = UUID.randomUUID();
        when(gameRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.joinAsUser(publicId, UUID.randomUUID())
        );

        assertEquals("Game not found", exception.getMessage());
        verifyNoInteractions(userRepository, participantRepository);
    }

    @Test
    void joiningWithMissingUserThrowsResourceNotFound() {
        UUID publicId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(gameRepository.findByPublicId(publicId)).thenReturn(Optional.of(mock(Game.class)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.joinAsUser(publicId, userId)
        );

        assertEquals("User not found", exception.getMessage());
        verifyNoInteractions(participantRepository);
    }

    @Test
    void joiningMissingGameAsAnonymousThrowsResourceNotFound() {
        UUID publicId = UUID.randomUUID();
        when(gameRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.joinAsAnonymous(publicId, "unused", null)
        );

        assertEquals("Game not found", exception.getMessage());
        verifyNoInteractions(jwtService, identityRepository, participantRepository);
    }
}
