package com.cassinocards.cassino_api.model.game;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "game_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "game_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, unique = true)
    private UUID gameId;

    @Column(name = "player_one_username", nullable = false)
    private String playerOneUsername;

    @Column(name = "player_one_score", nullable = false)
    private String playerOneScore;

    @Column(name = "player_two_username", nullable = false)
    private String playerTwoUsername;

    @Column(name = "player_two_score", nullable = false)
    private String playerTwoScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
