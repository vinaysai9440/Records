package com.Stats.Records.Service;
import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Request.PlayerStatsRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.Response.PlayerStatsResponse;

import java.util.List;

public interface PlayerService {
    PlayerResponse savePlayer(PlayerRequest request);
    PlayerResponse saveStats(PlayerStatsRequest request);
    PlayerStatsResponse getStats(String playerName, String format);
    List<String> getAllPlayerNames();
}
