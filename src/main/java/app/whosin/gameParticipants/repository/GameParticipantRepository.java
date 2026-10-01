package app.whosin.gameParticipants.repository;

import app.whosin.gameParticipants.entity.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, UUID> {
    boolean existsByGameIdAndUserId(UUID gameId, UUID userId);
    boolean existsByGameIdAndAnonymousIdentityId(UUID gameId, UUID anonymousIdentityId);
}
