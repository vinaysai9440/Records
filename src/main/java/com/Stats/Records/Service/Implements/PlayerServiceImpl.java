package com.Stats.Records.Service.Implements;


import com.Stats.Records.Repository.PlayerFormatStatsRepository;
import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Request.PlayerStatsRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.Response.PlayerStatsResponse;
import com.Stats.Records.Service.PlayerService;
import com.Stats.Records.entites.Format;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerFormatStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerFormatStatsRepository playerFormatStatsRepository;

    @Override
    public PlayerResponse savePlayer(PlayerRequest request) {
        if (playerRepository.findByPlayerNameIgnoreCase(request.getPlayerName()).isPresent()) {
            return new PlayerResponse(409, "Player already exists");
        }
        Player player = new Player();
        player.setPlayerName(request.getPlayerName());
        player.setCountry(request.getCountry());
        playerRepository.save(player);
        return new PlayerResponse(200, "Player saved");
    }

    @Override
    public PlayerResponse saveStats(PlayerStatsRequest request) {
        Format format = parseFormat(request.getFormat());
        if (format == null) {
            return new PlayerResponse(400, "Format must be one of TEST, ODI, T20");
        }

        Player player = playerRepository.findByPlayerNameIgnoreCase(request.getPlayerName())
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setPlayerName(request.getPlayerName());
                    newPlayer.setCountry(request.getCountry());
                    return playerRepository.save(newPlayer);
                });

        PlayerFormatStats stats = playerFormatStatsRepository
                .findByPlayer_PlayerIdAndFormat(player.getPlayerId(), format)
                .orElseGet(PlayerFormatStats::new);

        stats.setPlayer(player);
        stats.setFormat(format);
        stats.setMatches(request.getMatches());
        stats.setInnings(request.getInnings());
        stats.setRuns(request.getRuns());
        stats.setHundreds(request.getHundreds());
        stats.setFifties(request.getFifties());
        stats.setFours(request.getFours());
        stats.setSixes(request.getSixes());
        stats.setBallsFaced(request.getBallsFaced());
        playerFormatStatsRepository.save(stats);

        return new PlayerResponse(200, "Stats saved");
    }

    @Override
    public PlayerStatsResponse getStats(String playerName, String format) {
        Format parsedFormat = parseFormat(format);
        if (parsedFormat == null) {
            return PlayerStatsResponse.builder()
                    .statuscode(400)
                    .message("Format must be one of TEST, ODI, T20")
                    .build();
        }

        Optional<Player> playerOpt = playerRepository.findByPlayerNameIgnoreCase(playerName);
        if (playerOpt.isEmpty()) {
            return PlayerStatsResponse.builder()
                    .statuscode(404)
                    .message("Player not found")
                    .build();
        }

        Player player = playerOpt.get();
        Optional<PlayerFormatStats> statsOpt = playerFormatStatsRepository
                .findByPlayer_PlayerIdAndFormat(player.getPlayerId(), parsedFormat);

        if (statsOpt.isEmpty()) {
            return PlayerStatsResponse.builder()
                    .statuscode(404)
                    .message("No " + parsedFormat + " stats found for this player")
                    .playerName(player.getPlayerName())
                    .country(player.getCountry())
                    .format(parsedFormat.name())
                    .build();
        }

        PlayerFormatStats stats = statsOpt.get();
        double average = stats.getInnings() > 0 ? (double) stats.getRuns() / stats.getInnings() : 0;
        double strikeRate = stats.getBallsFaced() > 0 ? (double) stats.getRuns() / stats.getBallsFaced() * 100 : 0;

        return PlayerStatsResponse.builder()
                .statuscode(200)
                .message("Player stats found")
                .playerName(player.getPlayerName())
                .country(player.getCountry())
                .format(parsedFormat.name())
                .matches(stats.getMatches())
                .innings(stats.getInnings())
                .runs(stats.getRuns())
                .hundreds(stats.getHundreds())
                .fifties(stats.getFifties())
                .fours(stats.getFours())
                .sixes(stats.getSixes())
                .ballsFaced(stats.getBallsFaced())
                .average(Math.round(average * 100.0) / 100.0)
                .strikeRate(Math.round(strikeRate * 100.0) / 100.0)
                .build();
    }

    @Override
    public List<String> getAllPlayerNames() {
        return playerRepository.findAllByOrderByPlayerNameAsc()
                .stream()
                .map(Player::getPlayerName)
                .toList();
    }

    private Format parseFormat(String format) {
        try {
            return Format.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }
}
