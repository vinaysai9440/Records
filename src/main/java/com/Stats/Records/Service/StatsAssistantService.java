package com.Stats.Records.Service;

import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.Response.AssistantQueryResponse;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StatsAssistantService {

    private static final Pattern TOP_RANKING_PATTERN = Pattern.compile(
            "top\\s+(\\d+)\\s+.*?by\\s+([a-z_\\s-]+?)(?:\\s+in\\s+(test|odi|t20))?(?:\\s|$)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern AVERAGE_PATTERN = Pattern.compile(
            "(?:average|avg)\\s+(?:of|for)?\\s*([a-z .'-]+?)(?:\\s+from\\s+([a-z .'-]+))?$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern FIND_PLAYER_PATTERN = Pattern.compile(
            "(?:find|get|show)\\s+player\\s+([a-z .'-]+?)(?:\\s+from\\s+([a-z .'-]+))?$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAYER_RANKING_HISTORY_PATTERN = Pattern.compile(
            "(?:find|get|show)?\\s*(?:the\\s+)?rankings?\\s+(?:of|for)\\s+([a-z .'-]+?)(?:\\s+by\\s+([a-z_\\s-]+))?(?:\\s+in\\s+(test|odi|t20))?$",
            Pattern.CASE_INSENSITIVE);

    private final PlayerRankingService playerRankingService;
    private final PlayerRepository playerRepository;

    public AssistantQueryResponse ask(String query) {
        if (query == null || query.isBlank()) {
            return AssistantQueryResponse.builder()
                    .statusCode(400)
                    .intent("unsupported")
                    .message("Query cannot be empty")
                    .build();
        }

        String normalized = query.trim().toLowerCase(Locale.ROOT);

        AssistantQueryResponse rankingResponse = tryTopRankings(query, normalized);
        if (rankingResponse != null) {
            return rankingResponse;
        }

        AssistantQueryResponse playerRankingHistoryResponse = tryPlayerRankingHistory(query, normalized);
        if (playerRankingHistoryResponse != null) {
            return playerRankingHistoryResponse;
        }

        AssistantQueryResponse avgResponse = tryAverage(query, normalized);
        if (avgResponse != null) {
            return avgResponse;
        }

        AssistantQueryResponse findPlayerResponse = tryFindPlayer(query, normalized);
        if (findPlayerResponse != null) {
            return findPlayerResponse;
        }

        return AssistantQueryResponse.builder()
                .statusCode(400)
                .intent("unsupported")
                .message("Unsupported query. Try: 'top 5 batters by average in test', 'find rankings of Virat Kohli by runs', 'average of Virat Kohli from India', or 'find player Babar Azam'.")
                .build();
    }

    private AssistantQueryResponse tryTopRankings(String query, String normalized) {
        if (!normalized.contains("top") || !normalized.contains("by")) {
            return null;
        }

        Matcher matcher = TOP_RANKING_PATTERN.matcher(query);
        if (!matcher.find()) {
            return null;
        }

        int limit = Integer.parseInt(matcher.group(1));
        String metric = normalizeMetric(matcher.group(2));
        String format = matcher.group(3) == null ? "TEST" : matcher.group(3).toUpperCase(Locale.ROOT);

        List<PlayerRanking> rankings = playerRankingService.getTopRankings(metric, format, limit);
        Map<String, Object> payload = new HashMap<>();
        payload.put("metric", metric);
        payload.put("format", format);
        payload.put("limit", limit);
        payload.put("results", rankings);

        return AssistantQueryResponse.builder()
                .statusCode(200)
                .intent("top_rankings")
                .toolUsed("getTopRankings")
                .message("Top rankings fetched successfully")
                .data(payload)
                .build();
    }

    private AssistantQueryResponse tryPlayerRankingHistory(String query, String normalized) {
        if (!normalized.contains("ranking")) {
            return null;
        }

        Matcher matcher = PLAYER_RANKING_HISTORY_PATTERN.matcher(query.trim());
        if (!matcher.find()) {
            return null;
        }

        String playerName = matcher.group(1).trim();
        String metric = matcher.group(2) == null ? "runs" : normalizeMetric(matcher.group(2));
        String format = matcher.group(3) == null ? "TEST" : matcher.group(3).toUpperCase(Locale.ROOT);

        Player player = playerRepository.findByPlayerNameIgnoreCase(playerName);
        if (player == null) {
            return notFound(playerName, null);
        }

        List<PlayerRanking> rankingHistory = playerRankingService.getPlayerRankingHistory(player.getPlayerId(), metric, format);

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerName", player.getPlayerName());
        payload.put("metric", metric);
        payload.put("format", format);
        payload.put("results", rankingHistory);

        return AssistantQueryResponse.builder()
                .statusCode(200)
                .intent("player_rankings")
                .toolUsed("getPlayerRankingHistory")
                .message("Player ranking history fetched successfully")
                .data(payload)
                .build();
    }

    private AssistantQueryResponse tryAverage(String query, String normalized) {
        if (!normalized.contains("average") && !normalized.contains("avg")) {
            return null;
        }

        Matcher matcher = AVERAGE_PATTERN.matcher(query.trim());
        if (!matcher.find()) {
            return null;
        }

        String playerName = matcher.group(1).trim();
        String country = matcher.group(2) != null ? matcher.group(2).trim() : null;
        Player player = findPlayer(playerName, country);

        if (player == null) {
            return notFound(playerName, country);
        }

        double average = player.getInnings() > 0 ? (double) player.getRuns() / player.getInnings() : 0.0;

        Map<String, Object> payload = new HashMap<>();
        payload.put("playerName", player.getPlayerName());
        payload.put("country", player.getCountry());
        payload.put("innings", player.getInnings());
        payload.put("runs", player.getRuns());
        payload.put("average", average);

        return AssistantQueryResponse.builder()
                .statusCode(200)
                .intent("player_average")
                .toolUsed("findPlayer")
                .message("Player average calculated successfully")
                .data(payload)
                .build();
    }

    private AssistantQueryResponse tryFindPlayer(String query, String normalized) {
        if (!normalized.contains("player")) {
            return null;
        }

        Matcher matcher = FIND_PLAYER_PATTERN.matcher(query.trim());
        if (!matcher.find()) {
            return null;
        }

        String playerName = matcher.group(1).trim();
        String country = matcher.group(2) != null ? matcher.group(2).trim() : null;
        Player player = findPlayer(playerName, country);

        if (player == null) {
            return notFound(playerName, country);
        }

        return AssistantQueryResponse.builder()
                .statusCode(200)
                .intent("player_lookup")
                .toolUsed("findPlayer")
                .message("Player found")
                .data(player)
                .build();
    }

    private Player findPlayer(String playerName, String country) {
        if (country != null && !country.isBlank()) {
            return playerRepository.findByPlayerNameAndCountry(playerName, country);
        }
        return playerRepository.findByPlayerNameIgnoreCase(playerName);
    }

    private AssistantQueryResponse notFound(String playerName, String country) {
        String message = country == null || country.isBlank()
                ? "Player not found: " + playerName
                : "Player not found: " + playerName + " from " + country;

        return AssistantQueryResponse.builder()
                .statusCode(404)
                .intent("not_found")
                .message(message)
                .build();
    }

    private String normalizeMetric(String rawMetric) {
        String metric = rawMetric.toLowerCase(Locale.ROOT).trim().replace('-', '_').replace(' ', '_');
        if (metric.contains("strike")) {
            return "strike_rate";
        }
        if (metric.contains("avg")) {
            return "average";
        }
        if (metric.contains("run")) {
            return "runs";
        }
        return metric;
    }
}
