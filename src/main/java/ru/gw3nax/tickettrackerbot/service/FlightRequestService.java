package ru.gw3nax.tickettrackerbot.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.core.convert.ConversionService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaProducerProperties;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.producer.QueryProducer;
import ru.gw3nax.tickettrackerbot.repository.FlightRequestRepository;
import ru.gw3nax.tickettrackerbot.utils.CallbackQueryParser;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightRequestService {

    private final FlightRequestRepository flightRequestRepository;
    private final ConversionService conversionService;
    private final KafkaProducerProperties kafkaProducerProperties;
    private final KafkaTemplate<String, FlightRequest> kafkaTemplate;

    public List<FlightRequestEntity> findAllRequestsByUserId(String userId) {
        return flightRequestRepository.findByUserId(userId);
    }

    public InlineKeyboardInfo getAllRequestsByUserId(String userId) {
        var requests = flightRequestRepository.findByUserId(userId);
        return new InlineKeyboardInfo(getTotalPageNumber(requests.size()),
                requests.stream().map(InlineKeyboardInfo::getButtonInfo).toList());
    }

    private int getTotalPageNumber(int listSize) {
        return (listSize + 2) / 3;
    }
    public void saveFlightRequest(FlightRequest flightRequest) throws ExecutionException, InterruptedException {
        log.info(flightRequest.toString());
        List<Header> headers = List.of(
                new RecordHeader("client-name", "bot".getBytes(StandardCharsets.UTF_8)),//TODO поменять на поле из конфига
                new RecordHeader("action", flightRequest.getAction().toString().getBytes(StandardCharsets.UTF_8)),
                new RecordHeader("priority", "high".getBytes(StandardCharsets.UTF_8))
        );

        ProducerRecord<String, FlightRequest> record = new ProducerRecord<>(
                kafkaProducerProperties.topicProp().name(),
                null,
                null,
                null,
                flightRequest,
                headers
        );
        var result = kafkaTemplate.send(record).get();
        if (result != null) {
            log.info("Message sent successfully: {}", flightRequest);
        } else {
            log.error("Failed to send message: {}", flightRequest);
        }
        flightRequestRepository.save(Objects.requireNonNull(conversionService.convert(flightRequest, FlightRequestEntity.class)));
    }

    public void removeFlightRequest(Long requestId) throws ExecutionException, InterruptedException {
        var optionalFlightRequestEntity = flightRequestRepository.findById(requestId);
        if (optionalFlightRequestEntity.isEmpty()) throw new RuntimeException("No flight request found");//TODO edit
        var flightRequestEntity = optionalFlightRequestEntity.get();
        var flightRequest = conversionService.convert(flightRequestEntity, FlightRequest.class);

        List<Header> headers = List.of(
                new RecordHeader("client-name", "bot".getBytes(StandardCharsets.UTF_8)),//TODO поменять на поле из конфига
                new RecordHeader("action", "delete".getBytes(StandardCharsets.UTF_8)),
                new RecordHeader("priority", "high".getBytes(StandardCharsets.UTF_8))
        );

        ProducerRecord<String, FlightRequest> record = new ProducerRecord<>(
                kafkaProducerProperties.topicProp().name(),
                null,
                null,
                null,
                flightRequest,
                headers
        );
        var result = kafkaTemplate.send(record).get();
        if (result != null) {
            log.info("Message sent successfully: {}", flightRequest);
        } else {
            log.error("Failed to send message: {}", flightRequest);
        }
        flightRequestRepository.deleteById(requestId);
    }
}
