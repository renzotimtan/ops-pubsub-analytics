package com.renzotimtan.ops_analytics.subscriber;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import com.renzotimtan.ops_analytics.model.OrderEvent;
import com.renzotimtan.ops_analytics.service.AnalyticsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderEventSubscriber {

    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper;
    private final PubSubTemplate pubSubTemplate;

    public OrderEventSubscriber(AnalyticsService analyticsService, ObjectMapper objectMapper, PubSubTemplate pubSubTemplate) {
        this.analyticsService = analyticsService;
        this.objectMapper = objectMapper;
        this.pubSubTemplate = pubSubTemplate;

        // Start listening when the service is created
        subscribeToPubSub();
    }

    private void subscribeToPubSub() {
        pubSubTemplate.subscribe("ops-subscription", (message) -> {
            try {
                PubsubMessage pubsubMessage = message.getPubsubMessage();
                String payload = pubsubMessage.getData().toStringUtf8();

                log.info("Received Pub/Sub message: ", payload);
                
                // If not JSON, ignore
                if (!isValidJson(payload)) {
                    log.error("Invalid message format: Not a JSON object: {}", payload);
                    message.ack();
                    return;
                }

                // Convert JSON string to OrderEvent object
                OrderEvent orderEvent = objectMapper.readValue(payload, OrderEvent.class);

                // Process the order event
                analyticsService.processOrder(orderEvent);

                // Acknowledge message (important to prevent redelivery)
                message.ack();
                log.info("Successfully processed order event.");
            } catch (Exception e) {
                log.error("Failed to process order event: {}", e.getMessage(), e);
            }
        });
    }

    // Utility method to validate if a string is a JSON object
    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json); // Tries to parse the string
            return true;
        } catch (Exception e) {
            return false; // Not valid JSON
        }
    }
}
