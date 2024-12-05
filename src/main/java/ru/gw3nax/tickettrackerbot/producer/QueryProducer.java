package ru.gw3nax.tickettrackerbot.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaProducerProperties;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Component
@Slf4j
@Getter
public class QueryProducer {
    private final KafkaProducerProperties kafkaProperties;
    private final KafkaTemplate<String, FlightRequest> kafkaTemplate;

    @Value("${app.topic-name}")
    private String topicName;

    public void sendUpdate(FlightRequest flightRequest) throws InterruptedException, ExecutionException {
        log.info(flightRequest.getAction().toString());
        List<Header> headers = List.of(
                new RecordHeader("client-name", topicName.getBytes(StandardCharsets.UTF_8)),
                new RecordHeader("action", flightRequest.getAction().toString().getBytes(StandardCharsets.UTF_8)),
                new RecordHeader("priority", "high".getBytes(StandardCharsets.UTF_8))
        );

        ProducerRecord<String, FlightRequest> record = new ProducerRecord<>(
                kafkaProperties.topicProp().name(),
                null,
                null,
                null,
                flightRequest,
                headers
        );

        CompletableFuture<SendResult<String, FlightRequest>> future = kafkaTemplate.send(record);
        SendResult<String, FlightRequest> result = future.get();

        if (result != null) {
            log.info("Message sent successfully: {}", flightRequest);
        } else {
            log.error("Failed to send message: {}", flightRequest);
        }
    }
}
