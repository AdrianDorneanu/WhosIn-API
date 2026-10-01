package app.whosin.gameParticipants.entity;

import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import app.whosin.games.entity.Game;
import app.whosin.users.entity.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_participants")
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anonymous_identity_id")
    private AnonymousIdentity anonymousIdentity;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected GameParticipant() {
    }

    public static GameParticipant createForUser(
            Game game,
            User user,
            String displayName
    ) {
        GameParticipant participant = new GameParticipant();

        participant.game = game;
        participant.user = user;
        participant.anonymousIdentity = null;
        participant.displayName = displayName;

        return participant;
    }

    public static GameParticipant createForAnonymous(
            Game game,
            AnonymousIdentity anonymousIdentity,
            String displayName
    ) {
        GameParticipant participant = new GameParticipant();

        participant.game = game;
        participant.user = null;
        participant.anonymousIdentity = anonymousIdentity;
        participant.displayName = displayName;

        return participant;
    }

    public void assignToUser(User user) {
        this.user = user;
        this.anonymousIdentity = null;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();

        this.joinedAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }

    public AnonymousIdentity getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}