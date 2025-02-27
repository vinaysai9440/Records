package com.Stats.Records.entites;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players", schema = "player_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  Player{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(nullable = false, unique = true)
    private String playerName;

    @Column(nullable = false)
    private String country;
    private int innings;
    private int runs;
    private int hundreds;
    private int fifties;
    private int sixes;
    private int fours;


}
