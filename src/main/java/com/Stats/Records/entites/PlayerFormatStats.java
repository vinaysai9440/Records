package com.Stats.Records.entites;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "player_format_stats",
        schema = "player_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "format"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerFormatStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Format format;

    private int matches;
    private int innings;
    private int runs;
    private int hundreds;
    private int fifties;
    private int fours;
    private int sixes;
    private int ballsFaced;
}
