package com.Stats.Records.Controller;

import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Request.PlayerStatsRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.Response.PlayerStatsResponse;
import com.Stats.Records.Service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/stats")
    public ResponseEntity<PlayerStatsResponse> findPl(@RequestParam String playerName,
                                                        @RequestParam String format) {
        PlayerStatsResponse response = playerService.getStats(playerName, format);
        return ResponseEntity.status(response.getStatuscode()).body(response);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllPlayerNames() {
        return ResponseEntity.ok(playerService.getAllPlayerNames());
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> savePlayer(@RequestBody PlayerRequest request) {
        PlayerResponse response = playerService.savePlayer(request);
        return ResponseEntity.status(response.getStatuscode()).body(response);
    }

    @PostMapping("/stats")
    public ResponseEntity<PlayerResponse> saveStats(@RequestBody PlayerStatsRequest request) {
        PlayerResponse response = playerService.saveStats(request);
        return ResponseEntity.status(response.getStatuscode()).body(response);
    }
}
