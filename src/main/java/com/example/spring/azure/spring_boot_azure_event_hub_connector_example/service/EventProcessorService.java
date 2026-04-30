package com.example.spring.azure.spring_boot_azure_event_hub_connector_example.service;

import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class EventProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessorService.class);

    @Value("${spring.cloud.azure.eventhubs.connection-string}")
    private String eventHubsConnectionString;

    @Value("${spring.cloud.azure.eventhubs.event-hub-name}")
    private String eventHubName;

    @Value("${spring.cloud.azure.eventhubs.processor.consumer-group}")
    private String consumerGroup;

    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String storageConnectionString;

    @Value("${spring.cloud.azure.eventhubs.processor.checkpoint-store.container-name}")
    private String containerName;

    private EventProcessorClient processorClient;

    @PostConstruct
    public void startEventProcessor() {
        try {
            BlobContainerAsyncClient containerClient = new BlobContainerClientBuilder()
                    .connectionString(storageConnectionString)
                    .containerName(containerName)
                    .buildAsyncClient();

            processorClient = new EventProcessorClientBuilder()
                    .connectionString(eventHubsConnectionString)
                    .consumerGroup(consumerGroup)
                    .eventHubName(eventHubName)
                    .checkpointStore(new BlobCheckpointStore(containerClient))
                    .processEvent(eventContext -> {
                        String body = new String(eventContext.getEventData().getBody());
                        LOGGER.info("Processing event from partition {} with sequence number {} with body: {}",
                            eventContext.getPartitionContext().getPartitionId(),
                            eventContext.getEventData().getSequenceNumber(),
                            body);
                        eventContext.updateCheckpoint();
                    })
                    .processError(errorContext -> {
                        LOGGER.error("Error occurred in partition processor for partition {}, error: {}",
                            errorContext.getPartitionContext().getPartitionId(),
                            errorContext.getThrowable().getMessage());
                    })
                    .buildEventProcessorClient();

            processorClient.start();
            LOGGER.info("Event processor started successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to start event processor", e);
            throw new RuntimeException("Failed to start event processor", e);
        }
    }

    @PreDestroy
    public void stopEventProcessor() {
        if (processorClient != null) {
            processorClient.stop();
            LOGGER.info("Event processor stopped");
        }
    }
}
