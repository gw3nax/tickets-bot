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

    public InlineKeyboardInfo getAllRequestsByUserId(Integer pageNumber, Integer pageSize, Long userId) {
        var requests = flightRequestRepository.findByUserId(userId);
        var totalPageNumber = getTotalPageNumber(requests.size(), pageSize);
        var pageRequests = requests.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .map(InlineKeyboardInfo::getButtonInfo)
                .toList();
        return new InlineKeyboardInfo(totalPageNumber, pageRequests);
    }

    private int getTotalPageNumber(int listSize, int pageSize) {
        return (listSize + (pageSize-1)) / pageSize;
    }

    public void saveFlightRequest(FlightRequest flightRequest) {
        log.info("Saving flight request: {}", flightRequest);
        queryProducer.sendUpdate(flightRequest);
        var flightRequestEntity = Objects.requireNonNull(conversionService.convert(flightRequest, FlightRequestEntity.class));
        flightRequestEntity.setUser(userService.getUser(Long.valueOf(flightRequest.getUserId())));
        flightRequestRepository.save(flightRequestEntity);
    }

    public void removeFlightRequest(Long requestId, Long userId) {
        var optionalFlightRequestEntity = flightRequestRepository.findById(requestId);
        if (optionalFlightRequestEntity.isEmpty()) throw new RuntimeException("No flight request found");
        var flightRequestEntity = optionalFlightRequestEntity.get();
        flightRequestRepository.deleteById(requestId);
        var flightRequest = Objects.requireNonNull(conversionService.convert(flightRequestEntity, FlightRequest.class));
        flightRequest.setAction(Action.DELETE);
        flightRequest.setUserId(userId.toString());
        queryProducer.sendUpdate(flightRequest);
    }
}
