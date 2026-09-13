package com.Stats.Records.Service;

import com.Stats.Records.Repository.PlayerFormatStatsRepository;
import com.Stats.Records.Repository.PlayerRankingRepository;
import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.entites.Format;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerFormatStats;
import com.Stats.Records.entites.PlayerRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerRankingService {

    private final PlayerRankingRepository playerRankingRepository;
    private final PlayerRepository playerRepository;
    private final PlayerFormatStatsRepository playerFormatStatsRepository;

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

        for (Format format : Format.values()) {
            updateRankingsByMetric(players, "runs", format, today);
            updateRankingsByMetric(players, "average", format, today);
            updateRankingsByMetric(players, "strike_rate", format, today);
        }
    }

    private void updateRankingsByMetric(List<Player> players, String metric, Format format, LocalDate date) {
        List<PlayerFormatStats> sortedStats = players.stream()
                .map(player -> playerFormatStatsRepository
                        .findByPlayer_PlayerIdAndFormat(player.getPlayerId(), format)
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted((s1, s2) -> Double.compare(getMetricValue(s2, metric), getMetricValue(s1, metric)))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedStats.size(); i++) {
            PlayerFormatStats stats = sortedStats.get(i);
            PlayerRanking ranking = PlayerRanking.builder()
                    .player(stats.getPlayer())
                    .rankingDate(date)
                    .ranking(i + 1)
                    .metric(metric)
                    .metricValue(getMetricValue(stats, metric))
                    .format(format.name())
                    .build();
            playerRankingRepository.save(ranking);
        }
    }

    private double getMetricValue(PlayerFormatStats stats, String metric) {
        switch (metric) {
            case "runs":
                return stats.getRuns();
            case "average":
                return stats.getInnings() > 0 ? (double) stats.getRuns() / stats.getInnings() : 0;
            case "strike_rate":
                return stats.getBallsFaced() > 0 ? (double) stats.getRuns() / stats.getBallsFaced() * 100 : 0;
            default:
                return 0;
        }
    }
} 