package com.gmail.zerosetup.survivalg.repository;
import com.gmail.zerosetup.survivalg.model.Game;
import com.gmail.zerosetup.survivalg.model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByOrderByCreatedAtDesc();
    Optional<Game> findFirstByStatusOrderByStartDateTimeAsc(GameStatus status);
}
