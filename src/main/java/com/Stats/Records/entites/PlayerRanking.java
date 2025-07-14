package com.Stats.Records.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "player_rankings", schema = "player_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankingId;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private LocalDate rankingDate;

    @Column(nullable = false)
    private int ranking;

    @Column(nullable = false)
    private String metric; // e.g., "runs", "average", "strike_rate"

    @Column(nullable = false)
    private double metricValue;

    @Column(nullable = false)
    private String format; // e.g., "TEST", "ODI", "T20"
} 