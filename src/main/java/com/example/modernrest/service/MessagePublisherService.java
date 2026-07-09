package com.example.modernrest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisherService {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisherService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public MessagePublisherService(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.exchange:modern.exchange}") String exchange,
            @Value("${app.messaging.routing-key:modern.key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(String payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        log.info("Published message to exchange={} routingKey={}", exchange, routingKey);
    }
}
