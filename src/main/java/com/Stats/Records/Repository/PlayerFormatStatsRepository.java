package com.Stats.Records.Repository;

import com.Stats.Records.entites.Format;
import com.Stats.Records.entites.PlayerFormatStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerFormatStatsRepository extends JpaRepository<PlayerFormatStats, Long> {
    Optional<PlayerFormatStats> findByPlayer_PlayerIdAndFormat(Long playerId, Format format);
}
