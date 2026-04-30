package com.example.spring.azure.spring_boot_azure_event_hub_connector_example.api;

import java.util.Collections;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;


@RestController
public class MainController {

    private final EventHubProducerClient eventHubProducerClient;

    public MainController(EventHubProducerClient eventHubProducerClient) {
        this.eventHubProducerClient = eventHubProducerClient;
    }
    
    @PostMapping("/send")
    public String postMethodName(@RequestBody String entity) {
        eventHubProducerClient.send(Collections.singletonList( new EventData("Hello World!")));
        return "done";
    }
    
}
