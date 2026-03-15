package com.Stats.Records.Service;

import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.Response.AssistantQueryResponse;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsAssistantServiceTest {

    @Mock
    private PlayerRankingService playerRankingService;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private StatsAssistantService statsAssistantService;

    @Test
    void shouldResolveTopRankingsQuery() {
        Player player = new Player();
        player.setPlayerName("Virat Kohli");
        PlayerRanking ranking = PlayerRanking.builder().player(player).ranking(1).metric("average").format("TEST").build();

        when(playerRankingService.getTopRankings(eq("average"), eq("TEST"), eq(5)))
                .thenReturn(List.of(ranking));

        AssistantQueryResponse response = statsAssistantService.ask("top 5 batters by average in test");

        assertEquals(200, response.getStatusCode());
        assertEquals("top_rankings", response.getIntent());
        assertEquals("getTopRankings", response.getToolUsed());
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertEquals("average", data.get("metric"));
    }

    @Test
    void shouldResolvePlayerRankingHistoryQuery() {
        Player player = new Player();
        player.setPlayerId(10L);
        player.setPlayerName("Virat Kohli");

        PlayerRanking history = PlayerRanking.builder()
                .player(player)
                .ranking(2)
                .metric("runs")
                .format("TEST")
                .build();

        when(playerRepository.findByPlayerNameIgnoreCase("Virat Kohli")).thenReturn(player);
        when(playerRankingService.getPlayerRankingHistory(10L, "runs", "TEST"))
                .thenReturn(List.of(history));

        AssistantQueryResponse response = statsAssistantService.ask("find rankings of Virat Kohli");

        assertEquals(200, response.getStatusCode());
        assertEquals("player_rankings", response.getIntent());
        assertEquals("getPlayerRankingHistory", response.getToolUsed());
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertEquals("Virat Kohli", data.get("playerName"));
        assertEquals("runs", data.get("metric"));
    }

    @Test
    void shouldResolveAverageQueryByPlayerAndCountry() {
        Player player = new Player();
        player.setPlayerName("Virat Kohli");
        player.setCountry("India");
        player.setRuns(12000);
        player.setInnings(250);

        when(playerRepository.findByPlayerNameAndCountry("Virat Kohli", "India")).thenReturn(player);

        AssistantQueryResponse response = statsAssistantService.ask("average of Virat Kohli from India");

        assertEquals(200, response.getStatusCode());
        assertEquals("player_average", response.getIntent());
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertEquals(48.0, data.get("average"));
    }

    @Test
    void shouldReturnUnsupportedForUnknownQuery() {
        AssistantQueryResponse response = statsAssistantService.ask("summarize last week trends");

        assertEquals(400, response.getStatusCode());
        assertEquals("unsupported", response.getIntent());
        assertNotNull(response.getMessage());
    }
}
