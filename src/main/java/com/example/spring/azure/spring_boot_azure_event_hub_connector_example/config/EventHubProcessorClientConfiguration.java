package com.example.spring.azure.spring_boot_azure_event_hub_connector_example.config;

import com.azure.spring.cloud.service.eventhubs.consumer.EventHubsErrorHandler;
import com.azure.spring.cloud.service.eventhubs.consumer.EventHubsRecordMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubProcessorClientConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubProcessorClientConfiguration.class);

    @Bean
    EventHubsRecordMessageListener processEvent() {
        return eventContext->LOGGER.info("Processing event from partition {} with sequence number {} with body: {}",
            eventContext.getPartitionContext().getPartitionId(), eventContext.getEventData().getSequenceNumber(),
            eventContext.getEventData().getBodyAsString());
    }

    @Bean
    EventHubsErrorHandler processError() {
        return errorContext->LOGGER.info("Error occurred in partition processor for partition {}, {}",
            errorContext.getPartitionContext().getPartitionId(),
            errorContext.getThrowable());
    }

}
