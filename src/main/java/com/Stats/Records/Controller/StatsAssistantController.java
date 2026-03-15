package com.Stats.Records.Controller;

import com.Stats.Records.Request.AssistantQueryRequest;
import com.Stats.Records.Response.AssistantQueryResponse;
import com.Stats.Records.Service.StatsAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class StatsAssistantController {

    private final StatsAssistantService statsAssistantService;

    @PostMapping("/ask")
    public ResponseEntity<AssistantQueryResponse> ask(@RequestBody AssistantQueryRequest request) {
        AssistantQueryResponse response = statsAssistantService.ask(request.getQuery());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
