package com.Stats.Records.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor//This is going to create NOarg con
@AllArgsConstructor// This is going to create all arg const
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerResponse {
    private String playerName;
    private String country;
    private int innings;
    private int runs;
    private int hundreds;
    private int fifties;
    private int sixes;
    private int fours;
    private int statuscode;
    private String message;
    private Float avg;
}
