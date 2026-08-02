package app.whosin.games.entity;

import app.whosin.users.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "games",
        indexes = {
                @Index(name = "idx_games_organizer_id", columnList = "organizer_id"),
                @Index(name = "idx_games_starts_at", columnList = "starts_at"),
                @Index(name = "idx_games_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_games_public_id",
                        columnNames = "public_id"
                )
        }
)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "public_id",
            nullable = false,
            unique = true
    )
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organizer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_games_organizer")
    )
    private User organizer;

    @Column(
            name = "title",
            nullable = false
    )
    private String title;

    @Column(
            name = "sport",
            nullable = false
    )
    private String sport;

    @Column(
            name = "starts_at",
            nullable = false
    )
    private OffsetDateTime startsAt;

    @Column(
            name = "ends_at",
            nullable = false
    )
    private OffsetDateTime endsAt;

    @Column(
            name = "location",
            nullable = false
    )
    private String location;

    @Column(
            name = "max_players",
            nullable = false
    )
    private Integer maxPlayers;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private GameStatus status = GameStatus.UPCOMING;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Column(
            name = "cost_per_player",
            precision = 10,
            scale = 2
    )
    private BigDecimal costPerPlayer;

    @Column(
            name = "notes",
            length = 500
    )
    private String notes;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    protected Game() {
    }

    public Game(
            String publicId,
            User organizer,
            String title,
            String sport,
            OffsetDateTime startsAt,
            OffsetDateTime endsAt,
            String location,
            Integer maxPlayers,
            BigDecimal costPerPlayer,
            String notes
    ) {
        this.publicId = publicId;
        this.organizer = organizer;
        this.title = title;
        this.sport = sport;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
        this.maxPlayers = maxPlayers;
        this.costPerPlayer = costPerPlayer;
        this.notes = notes;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = GameStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
    }

    public void complete() {
        this.status = GameStatus.COMPLETED;
    }

    public void updateDetails(
            String title,
            String sport,
            OffsetDateTime startsAt,
            OffsetDateTime endsAt,
            String location,
            Integer maxPlayers,
            BigDecimal costPerPlayer,
            String notes
    ) {
        this.title = title;
        this.sport = sport;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
        this.maxPlayers = maxPlayers;
        this.costPerPlayer = costPerPlayer;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public User getOrganizer() {
        return organizer;
    }

    public String getTitle() {
        return title;
    }

    public String getSport() {
        return sport;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public String getLocation() {
        return location;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public GameStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public BigDecimal getCostPerPlayer() {
        return costPerPlayer;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}


