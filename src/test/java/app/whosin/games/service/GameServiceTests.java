package app.whosin.games.service;

import app.whosin.common.exception.ResourceNotFoundException;
import app.whosin.games.mapper.GameMapper;
import app.whosin.games.repository.GameRepository;
import app.whosin.users.entity.User;
import app.whosin.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GameServiceTests {
    private final GameRepository gameRepository = mock(GameRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameMapper gameMapper = mock(GameMapper.class);
    private final GameService service = new GameService(gameRepository, userRepository, gameMapper);

    @Test
    void missingGameThrowsResourceNotFound() {
        UUID publicId = UUID.randomUUID();
        when(gameRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.findByPublicId(publicId)
        );

        assertEquals("Game not found", exception.getMessage());
        verifyNoInteractions(gameMapper);
    }

    @Test
    void missingOrganizerThrowsResourceNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> service.createGame(userId, null)
        );

        assertEquals("User not found", exception.getMessage());
        verifyNoInteractions(gameRepository, gameMapper);
    }

    @Test
    void inactiveOrganizerStillThrowsAccessDenied() {
        UUID userId = UUID.randomUUID();
        User organizer = new User("test@example.com", null, "Test user", null, null);
        organizer.suspend();
        when(userRepository.findById(userId)).thenReturn(Optional.of(organizer));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class, () -> service.createGame(userId, null)
        );

        assertEquals("Only active users can create games", exception.getMessage());
        verifyNoInteractions(gameRepository, gameMapper);
    }
}
