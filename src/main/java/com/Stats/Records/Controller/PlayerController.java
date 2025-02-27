package com.Stats.Records.Controller;

import com.Stats.Records.Request.PlayerRequest;
import com.Stats.Records.Response.PlayerResponse;
import com.Stats.Records.entites.Player;
import com.Stats.Records.Service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
//It tells Spring that this class handles REST API requests and returns JSON/XML data instead of a view (HTML page).
@RequestMapping("/players")// This is the base URL for all methods in controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/save")
    public PlayerResponse savePl(@RequestBody PlayerRequest request) {
        PlayerResponse response = playerService.savePlayer(request);

        return response;
    }
    @GetMapping("/find")
    public PlayerResponse findPl(@RequestBody PlayerRequest request)
    {
        System.out.println("Received playerName in controller: " + request.getPlayerName() + ", country: " +request.getCountry()); // Debugging


        PlayerResponse response=playerService.findByPlayerNameAndCountry(request.getPlayerName(),request.getCountry());

        return response;

    }


}

