package com.Stats.Records.Request;


import lombok.*;

@Getter
@Setter
public class PlayerStatsRequest {
    private String playerName;
    private String country; // used to create the player if they don't exist yet
    private String format;  // TEST | ODI | T20
    private int matches;
    private int innings;
    private int runs;
    private int hundreds;
    private int fifties;
    private int fours;
    private int sixes;
    private int ballsFaced;
}
