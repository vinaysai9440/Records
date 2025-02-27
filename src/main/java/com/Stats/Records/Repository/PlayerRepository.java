package com.Stats.Records.Repository;


import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.entites.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
//If you want to deal with db and if you need any conditions we can do it from here ex: findByPlayerName.
//This rep will fetch the data from the db by taking help with the entite class.
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    //In Spring, you can use the Optional class from Java 8 to handle situations where a value might be absent. This can be particularly useful when dealing with return values from methods that might not always have a result.
    //We can call this (findByPlayerName(String playerName))---as contract.
    Player findByPlayerName(String playerName);//"findBy" is useredefined function and telling it to filter playernamr (in DB perspective)
    Player findByPlayerNameAndCountry(String playerName, String country);
}