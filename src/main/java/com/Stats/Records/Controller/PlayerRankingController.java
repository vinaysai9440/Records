package com.Stats.Records.Controller;

import com.Stats.Records.Service.PlayerRankingService;
import com.Stats.Records.entites.PlayerRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rankings")
@RequiredArgsConstructor
public class PlayerRankingController {

    private final PlayerRankingService playerRankingService;

    @GetMapping("/top")
    public ResponseEntity<List<PlayerRanking>> getTopRankings(
            @RequestParam String metric,
            @RequestParam(defaultValue = "TEST") String format,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playerRankingService.getTopRankings(metric, format, limit));
    }

    @GetMapping("/history/{playerId}")
    public ResponseEntity<List<PlayerRanking>> getPlayerRankingHistory(
            @PathVariable Long playerId,
            @RequestParam String metric,
            @RequestParam(defaultValue = "TEST") String format) {
        return ResponseEntity.ok(playerRankingService.getPlayerRankingHistory(playerId, metric, format));
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateRankings() {
        playerRankingService.updateRankings();
        return ResponseEntity.ok("Rankings updated successfully");
    }
} 