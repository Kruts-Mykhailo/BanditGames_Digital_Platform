package be.kdg.integration5.gameplatformcontext.core;

import be.kdg.integration5.gameplatformcontext.domain.*;
import be.kdg.integration5.gameplatformcontext.port.in.FindQuickMatchCommand;
import be.kdg.integration5.gameplatformcontext.port.in.FindQuickMatchUseCase;
import be.kdg.integration5.gameplatformcontext.port.out.FindLobbyPort;
import be.kdg.integration5.gameplatformcontext.port.out.FindPlayerPort;
import be.kdg.integration5.gameplatformcontext.port.out.NotifyLobbyUpdatePort;
import be.kdg.integration5.gameplatformcontext.port.out.PersistLobbyPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class FindQuickMatchUseCaseImpl implements FindQuickMatchUseCase {
    private final FindLobbyPort findLobbyPort;
    private final PersistLobbyPort persistLobbyPort;
    private final FindPlayerPort findPlayerPort;
    private final NotifyLobbyUpdatePort notifyLobbyUpdatePort;

    @Autowired
    public FindQuickMatchUseCaseImpl(FindLobbyPort findLobbyPort, PersistLobbyPort persistLobbyPort, FindPlayerPort findPlayerPort, NotifyLobbyUpdatePort notifyLobbyUpdatePort) {
        this.findLobbyPort = findLobbyPort;
        this.persistLobbyPort = persistLobbyPort;
        this.findPlayerPort = findPlayerPort;
        this.notifyLobbyUpdatePort = notifyLobbyUpdatePort;
    }

    @Override
    @Transactional
    public Lobby findQuickMatch(FindQuickMatchCommand findQuickMatchCommand) {
        PlayerId playerId = findQuickMatchCommand.playerId();
        Player player = findPlayerPort.findPlayerById(playerId);

        GameId gameId = findQuickMatchCommand.gameId();
        List<Lobby> lobbyList = findLobbyPort.findAllNotFilledNonPrivateLobbiesByGameId(gameId);

        Lobby selectedLobby = findLobbyForPlayer(lobbyList, player, gameId);
        selectedLobby.addPlayer(player);

        Lobby savedLobby = persistLobbyPort.save(selectedLobby);

        sendLobbyNotifications(savedLobby, playerId);

        return savedLobby;
    }

    private Lobby findLobbyForPlayer(Collection<Lobby> lobbies, Player player, GameId gameId) {
        Lobby selectedLobby;

        if (lobbies.isEmpty())
            selectedLobby = player.createNewPublicLobbyForGame(gameId);
        else
            selectedLobby = player.findBestLobbyMatch(lobbies);

        return selectedLobby;
    }

    private void sendLobbyNotifications(Lobby lobby, PlayerId playerId) {
        LobbyId lobbyId = lobby.getLobbyId();
        notifyLobbyUpdatePort.notifyPlayerJoinedLobby(lobbyId, playerId);

        if (lobby.isFull())
            notifyLobbyUpdatePort.notifyLobbyIsFull(lobby);
    }
}
