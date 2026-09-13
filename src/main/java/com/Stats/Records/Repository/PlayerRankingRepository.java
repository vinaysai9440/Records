package com.Stats.Records.Repository;

import com.Stats.Records.entites.PlayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlayerRankingRepository extends JpaRepository<PlayerRanking, Long> {

    @Query("SELECT r FROM PlayerRanking r WHERE r.metric = :metric AND r.format = :format AND r.rankingDate = :date ORDER BY r.ranking ASC")
    List<PlayerRanking> findTopRankingsByMetricAndFormat(@Param("metric") String metric,
                                                          @Param("format") String format,
                                                          @Param("date") LocalDate date);

    @Query("SELECT r FROM PlayerRanking r WHERE r.player.playerId = :playerId AND r.metric = :metric AND r.format = :format ORDER BY r.rankingDate DESC")
    List<PlayerRanking> findPlayerRankingHistory(@Param("playerId") Long playerId,
                                                  @Param("metric") String metric,
                                                  @Param("format") String format);
}
