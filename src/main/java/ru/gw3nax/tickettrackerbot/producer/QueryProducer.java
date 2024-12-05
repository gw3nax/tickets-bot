package ru.gw3nax.tickettrackerbot.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaProducerProperties;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
@Getter
public class QueryProducer {
    private final KafkaProducerProperties kafkaProperties;
    private final KafkaTemplate<String, FlightRequest> kafkaTemplate;

    @Value("${spring.application.name}")
    private String clientName;

    public void sendUpdate(FlightRequest flightRequest) {
        log.info(flightRequest.getAction().toString());
        List<Header> headers = List.of(
                new RecordHeader("client-name", clientName.getBytes(StandardCharsets.UTF_8)),
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

        kafkaTemplate.send(record);
    }
}
