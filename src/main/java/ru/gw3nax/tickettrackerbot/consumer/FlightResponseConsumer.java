package ru.gw3nax.tickettrackerbot.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.dto.response.FlightResponse;
import ru.gw3nax.tickettrackerbot.service.BotService;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
public class FlightResponseConsumer {
    private final BotService botService;

    @Value("${app.topic-name}")
    private String topicName;

    @RetryableTopic(
            attempts = "1",
            kafkaTemplate = "kafkaTemplate",
            dltTopicSuffix = "_dlq",
            include = RuntimeException.class
    )
    @KafkaListener(topics = "#{__listener.topicName}", groupId = "listen", containerFactory = "kafkaListener")
    public void listen(@Payload FlightResponse flightResponse, Acknowledgment acknowledgment) {
        log.info("Received flight response: {}", flightResponse);
        botService.update(flightResponse);
        acknowledgment.acknowledge();
    }
}