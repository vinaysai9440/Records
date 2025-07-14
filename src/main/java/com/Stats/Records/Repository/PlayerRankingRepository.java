package com.Stats.Records.Repository;

import com.Stats.Records.entites.PlayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlayerRankingRepository extends JpaRepository<PlayerRanking, Long> {
    
    @Query("SELECT pr FROM PlayerRanking pr WHERE pr.metric = :metric AND pr.format = :format AND pr.rankingDate = :date ORDER BY pr.ranking ASC")
    List<PlayerRanking> findTopRankingsByMetricAndFormat(
            @Param("metric") String metric,
            @Param("format") String format,
            @Param("date") LocalDate date);

    @Query("SELECT pr FROM PlayerRanking pr WHERE pr.player.playerId = :playerId AND pr.metric = :metric AND pr.format = :format ORDER BY pr.rankingDate ASC")
    List<PlayerRanking> findPlayerRankingHistory(
            @Param("playerId") Long playerId,
            @Param("metric") String metric,
            @Param("format") String format);
} 