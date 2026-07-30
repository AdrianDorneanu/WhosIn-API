package app.whosin.users.entity;

import app.whosin.games.entity.Game;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_status", columnList = "status")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "email_verified_at")
    private OffsetDateTime emailVerifiedAt;

    @Column
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(
            mappedBy = "organizer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Game> organizedGames = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(
            String email,
            String passwordHash,
            String displayName,
            String avatarUrl,
            String timezone
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.timezone = timezone;
        this.status = UserStatus.ACTIVE;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

    public void verifyEmail() {
        emailVerifiedAt = OffsetDateTime.now();
    }

    public void suspend() {
        status = UserStatus.SUSPENDED;
    }

    public void activate() {
        status = UserStatus.ACTIVE;
    }

    public void updateProfile(
            String displayName,
            String avatarUrl,
            String timezone
    ) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.timezone = timezone;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public OffsetDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public List<Game> getOrganizedGames() {
        return organizedGames;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}