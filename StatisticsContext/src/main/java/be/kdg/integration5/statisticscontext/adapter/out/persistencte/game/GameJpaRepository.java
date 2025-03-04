package be.kdg.integration5.statisticscontext.adapter.out.persistencte.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameJpaRepository extends JpaRepository<GameJpaEntity, UUID> {

    Optional<GameJpaEntity> findByName(String name);
}
