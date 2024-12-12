package ru.gw3nax.tickettrackerbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gw3nax.tickettrackerbot.dto.request.Action;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;
import ru.gw3nax.tickettrackerbot.exceptions.NoFlightRequestFoundException;
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

    @Transactional(readOnly = true)
    public List<FlightRequestEntity> findAllRequestsByUserId(Long userId) {
        var user = userService.getUser(userId);
        if (user == null) {
            throw new NoFlightRequestFoundException("User with ID " + userId + " not found");
        }
        return flightRequestRepository.findByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public InlineKeyboardInfo getAllRequestsByUserId(Integer pageNumber, Integer pageSize, Long userId) {
        var user = userService.getUser(userId);
        var requests = flightRequestRepository.findByUserId(user.getId());
        var totalPageNumber = getTotalPageNumber(requests.size(), pageSize);
        var pageRequests = requests.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .map(InlineKeyboardInfo::getButtonInfo)
                .toList();
        return new InlineKeyboardInfo(totalPageNumber, pageRequests);
    }

    private int getTotalPageNumber(int listSize, int pageSize) {
        return (listSize + (pageSize - 1)) / pageSize;
    }

    @Transactional
    public void saveFlightRequest(FlightRequest flightRequest) {
        log.info("Saving flight request: {}", flightRequest);

        var flightRequestEntity = conversionService.convert(flightRequest, FlightRequestEntity.class);
        if (flightRequestEntity == null) {
            throw new IllegalArgumentException("Conversion of FlightRequest to FlightRequestEntity failed");
        }

        var user = userService.getUser(Long.valueOf(flightRequest.getUserId()));
        if (user == null) {
            throw new NoFlightRequestFoundException("User with ID " + flightRequest.getUserId() + " not found");
        }

        flightRequestEntity.setUser(user);
        log.info("User Entity: {}", flightRequestEntity);

        flightRequestRepository.save(flightRequestEntity);

        queryProducer.sendUpdate(flightRequest);
    }

    @Transactional
    public void removeFlightRequest(Long requestId, Long userId) {
        var optionalFlightRequestEntity = flightRequestRepository.findById(requestId);
        if (optionalFlightRequestEntity.isEmpty()) {
            throw new NoFlightRequestFoundException("No flight request found for ID " + requestId);
        }

        var flightRequestEntity = optionalFlightRequestEntity.get();

        flightRequestRepository.deleteById(requestId);

        var flightRequest = conversionService.convert(flightRequestEntity, FlightRequest.class);
        if (flightRequest == null) {
            throw new IllegalArgumentException("Conversion of FlightRequestEntity to FlightRequest failed");
        }

        flightRequest.setAction(Action.DELETE);
        flightRequest.setUserId(userId.toString());

        queryProducer.sendUpdate(flightRequest);
    }

}
