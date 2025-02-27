package com.Stats.Records.Service;
import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.entites.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerService {
    PlayerResponse findByPlayerNameAndCountry(String playerName,String country);
    PlayerResponse savePlayer(PlayerRequest request);
}
