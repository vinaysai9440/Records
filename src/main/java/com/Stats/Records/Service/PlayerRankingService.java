package com.Stats.Records.Service;

import com.Stats.Records.Repository.PlayerRankingRepository;
import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerRankingService {

    private final PlayerRankingRepository playerRankingRepository;
    private final PlayerRepository playerRepository;

    public List<PlayerRanking> getTopRankings(String metric, String format, int limit) {
        return playerRankingRepository.findTopRankingsByMetricAndFormat(metric, format, LocalDate.now())
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<PlayerRanking> getPlayerRankingHistory(Long playerId, String metric, String format) {
        return playerRankingRepository.findPlayerRankingHistory(playerId, metric, format);
    }

    public void updateRankings() {
        List<Player> players = playerRepository.findAll();
        LocalDate today = LocalDate.now();

        // Update rankings for different metrics
        updateRankingsByMetric(players, "runs", today);
        updateRankingsByMetric(players, "average", today);
        updateRankingsByMetric(players, "strike_rate", today);
    }

    private void updateRankingsByMetric(List<Player> players, String metric, LocalDate date) {
        // Sort players based on the metric
        List<Player> sortedPlayers = players.stream()
                .sorted((p1, p2) -> {
                    double value1 = getMetricValue(p1, metric);
                    double value2 = getMetricValue(p2, metric);
                    return Double.compare(value2, value1); // Descending order
                })
                .collect(Collectors.toList());

        // Save rankings
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            PlayerRanking ranking = PlayerRanking.builder()
                    .player(player)
                    .rankingDate(date)
                    .ranking(i + 1)
                    .metric(metric)
                    .metricValue(getMetricValue(player, metric))
                    .format("TEST") // You can make this configurable
                    .build();
            playerRankingRepository.save(ranking);
        }
    }

    private double getMetricValue(Player player, String metric) {
        switch (metric) {
            case "runs":
                return player.getRuns();
            case "average":
                return player.getInnings() > 0 ? (double) player.getRuns() / player.getInnings() : 0;
            case "strike_rate":
                // Assuming you have balls faced in your Player entity
                // return player.getBallsFaced() > 0 ? (double) player.getRuns() / player.getBallsFaced() * 100 : 0;
                return 0; // Implement when you add balls faced to Player entity
            default:
                return 0;
        }
    }
} 