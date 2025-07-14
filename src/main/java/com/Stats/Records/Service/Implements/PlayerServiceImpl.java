package com.Stats.Records.Service.Implements;


import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.Service.PlayerService;
import com.Stats.Records.entites.Player;
import com.Stats.Records.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

//@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private PlayerRepository playerRepository;


    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    @Override
    public PlayerResponse findByPlayerNameAndCountry(String playerName, String country) {
        PlayerResponse playerResponse = new PlayerResponse();
        Player player = new Player();
        System.out.println("Received playerName: " + playerName + ", country: " + country); // Debugging

        player = playerRepository.findByPlayerNameAndCountry(playerName, country);
        if (player != null) {
            playerResponse.setPlayerName(player.getPlayerName());
            playerResponse.setCountry(player.getCountry());
            playerResponse.setInnings(player.getInnings());
            playerResponse.setHundreds(player.getHundreds());
            playerResponse.setFifties(player.getFifties());
            playerResponse.setRuns(player.getRuns());
            playerResponse.setSixes(player.getSixes());
            playerResponse.setFours(player.getFours());
            playerResponse.setStatuscode(200);
            playerResponse.setMessage("player found");
        } else {
            playerResponse.setStatuscode(404);
            playerResponse.setMessage("player not found");
        }


        return playerResponse;
    }

    public PlayerResponse savePlayer(PlayerRequest request) {
        PlayerResponse playerResponse = new PlayerResponse();
        Player player = new Player();
        player.setPlayerName(request.getPlayerName());
        player.setCountry(request.getCountry());
        player.setFifties(request.getFifties());
        player.setInnings(request.getInnings());
        player.setHundreds(request.getHundreds());
        player.setFours(request.getFours());
        player.setSixes(request.getSixes());
        player.setRuns(request.getRuns());
        playerRepository.save(player);
        playerResponse.setStatuscode(200);
        playerResponse.setMessage("Player saved");

        //playerRepository.save(request);
        return playerResponse;

    }



    public PlayerResponse batsMan_AVG(String PlayerName,String Country)
    {
        PlayerResponse playerResponse = new PlayerResponse();
        Player player = new Player();
        player = playerRepository.findByPlayerNameAndCountry(PlayerName,Country);
        int a= player.getRuns();
        int b= player.getInnings();
        Float C= (float) (a/b);
        playerResponse.setAvg(C);
        playerResponse.setStatuscode(200);
        playerResponse.setMessage("Bats man avg");
           //runs/innings

        return playerResponse;
    }

}


