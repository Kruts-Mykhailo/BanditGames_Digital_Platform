package be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.game;

import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.move.MoveJpaEntity;
import be.kdg.integration5.checkersachievementcontext.adapter.out.presistence.player.PlayerJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameJpaEntity {
    @Id
    @Column(name = "game_id", nullable = false, unique = true, updatable = false)
    private UUID gameId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "games_players",
            joinColumns = @JoinColumn(name = "game_id", referencedColumnName = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "player_id")
    )
    private List<PlayerJpaEntity> players;

    @OneToMany(mappedBy = "game")
    private List<MoveJpaEntity> moves;

    public GameJpaEntity(UUID gameId, List<PlayerJpaEntity> players) {
        this.gameId = gameId;
        this.players = players;
    }
}
