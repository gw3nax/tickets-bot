package ru.gw3nax.tickettrackerbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.dto.request.Action;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.producer.QueryProducer;
import ru.gw3nax.tickettrackerbot.repository.FlightRequestRepository;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightRequestService {

    private final FlightRequestRepository flightRequestRepository;
    private final ConversionService conversionService;
    private final QueryProducer queryProducer;
    private final UserService userService;

    public List<FlightRequestEntity> findAllRequestsByUserId(Long userId) {
        return flightRequestRepository.findByUserId(userId);
    }

    public InlineKeyboardInfo getAllRequestsByUserId(Long userId) {
        var requests = flightRequestRepository.findByUserId(userId);
        return new InlineKeyboardInfo(getTotalPageNumber(requests.size()),
                requests.stream().map(InlineKeyboardInfo::getButtonInfo).toList());
    }

    private int getTotalPageNumber(int listSize) {
        return (listSize + 2) / 3;
    }

    public void saveFlightRequest(FlightRequest flightRequest) {
        queryProducer.sendUpdate(flightRequest);
        var flightRequestEntity = Objects.requireNonNull(conversionService.convert(flightRequest, FlightRequestEntity.class));
        flightRequestEntity.setUser(userService.getUser(Long.valueOf(flightRequest.getUserId())));
        flightRequestRepository.save(flightRequestEntity);
    }

    public void removeFlightRequest(Long requestId) {
        var optionalFlightRequestEntity = flightRequestRepository.findById(requestId);
        if (optionalFlightRequestEntity.isEmpty()) throw new RuntimeException("No flight request found");
        var flightRequestEntity = optionalFlightRequestEntity.get();
        flightRequestRepository.deleteById(requestId);
        var flightRequest = Objects.requireNonNull(conversionService.convert(flightRequestEntity, FlightRequest.class));
        flightRequest.setAction(Action.DELETE);
        queryProducer.sendUpdate(flightRequest);
    }
}
