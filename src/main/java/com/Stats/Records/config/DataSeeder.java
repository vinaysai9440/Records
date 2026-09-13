package com.Stats.Records.config;

import com.Stats.Records.Repository.PlayerFormatStatsRepository;
import com.Stats.Records.Repository.PlayerRepository;
import com.Stats.Records.entites.Format;
import com.Stats.Records.entites.Player;
import com.Stats.Records.entites.PlayerFormatStats;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final PlayerFormatStatsRepository playerFormatStatsRepository;

    @Override
    public void run(String... args) {
        if (playerRepository.count() > 0) {
            return;
        }

        seedPlayer("Virat Kohli", "India");
        seedPlayer("Joe Root", "England");
        seedPlayer("Babar Azam", "Pakistan");
    }

    private void seedPlayer(String name, String country) {
        Player player = new Player();
        player.setPlayerName(name);
        player.setCountry(country);
        player = playerRepository.save(player);

        switch (name) {
            case "Virat Kohli" -> {
                saveStats(player, Format.TEST, 123, 210, 8848, 29, 30, 968, 24, 17000);
                saveStats(player, Format.ODI, 292, 280, 13906, 50, 72, 1233, 152, 15000);
                saveStats(player, Format.T20, 125, 117, 4188, 1, 38, 379, 128, 3200);
            }
            case "Joe Root" -> {
                saveStats(player, Format.TEST, 145, 264, 12716, 34, 62, 1421, 30, 22000);
                saveStats(player, Format.ODI, 174, 162, 6522, 16, 40, 570, 32, 7300);
                saveStats(player, Format.T20, 32, 30, 893, 0, 5, 84, 15, 700);
            }
            case "Babar Azam" -> {
                saveStats(player, Format.TEST, 54, 100, 4099, 10, 21, 460, 18, 7800);
                saveStats(player, Format.ODI, 117, 113, 5729, 20, 33, 540, 42, 6300);
                saveStats(player, Format.T20, 123, 117, 4223, 3, 33, 380, 66, 3300);
            }
        }
    }

    private void saveStats(Player player, Format format, int matches, int innings, int runs,
                            int hundreds, int fifties, int fours, int sixes, int ballsFaced) {
        playerFormatStatsRepository.save(PlayerFormatStats.builder()
                .player(player)
                .format(format)
                .matches(matches)
                .innings(innings)
                .runs(runs)
                .hundreds(hundreds)
                .fifties(fifties)
                .fours(fours)
                .sixes(sixes)
                .ballsFaced(ballsFaced)
                .build());
    }
}
