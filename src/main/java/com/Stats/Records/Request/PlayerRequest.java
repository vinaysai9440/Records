package com.Stats.Records.Request;


import lombok.*;
//This is a custom class (DTO - Data Transfer Object) that represents the structure of the JSON payload sent in the request body.
//is typically used to handle incoming API requests.

@Getter
@Setter
public class PlayerRequest {
    private String playerName;
    private String country;
}
