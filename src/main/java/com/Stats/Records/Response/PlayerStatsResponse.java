package com.Stats.Records.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerStatsResponse {
    private int statuscode;
    private String message;
    private String playerName;
    private String country;
    private String format;
    private int matches;
    private int innings;
    private int runs;
    private int hundreds;
    private int fifties;
    private int fours;
    private int sixes;
    private int ballsFaced;
    private double average;
    private double strikeRate;
}
