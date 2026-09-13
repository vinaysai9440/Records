package com.Stats.Records.Repository;


import com.Stats.Records.entites.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
//If you want to deal with db and if you need any conditions we can do it from here ex: findByPlayerName.
//This rep will fetch the data from the db by taking help with the entite class.
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByPlayerNameIgnoreCase(String playerName);
    List<Player> findAllByOrderByPlayerNameAsc();
}