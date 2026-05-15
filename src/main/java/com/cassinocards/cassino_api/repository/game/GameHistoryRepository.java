package com.cassinocards.cassino_api.repository.game;

import com.cassinocards.cassino_api.model.game.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    Optional<GameHistory> findByGameId(UUID gameId);
    List<GameHistory> findByPlayerOneUsernameOrPlayerTwoUsername(String p1, String p2);
}
