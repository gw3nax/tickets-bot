package ru.gw3nax.tickettrackerbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import ru.gw3nax.tickettrackerbot.dto.request.Action;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;
import ru.gw3nax.tickettrackerbot.entity.User;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.producer.QueryProducer;
import ru.gw3nax.tickettrackerbot.repository.FlightRequestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FlightRequestServiceTest {

    @Mock
    private FlightRequestRepository flightRequestRepository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private QueryProducer queryProducer;

    @Mock
    private UserService userService;

    private FlightRequestService flightRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightRequestService = new FlightRequestService(flightRequestRepository, conversionService, queryProducer, userService);
    }

    @Test
    void testFindAllRequestsByUserId() {
        Long userId = 1L;
        List<FlightRequestEntity> mockRequests = List.of(new FlightRequestEntity());
        when(flightRequestRepository.findByUserId(userId)).thenReturn(mockRequests);

        var result = flightRequestService.findAllRequestsByUserId(userId);

        assertEquals(mockRequests, result);
        verify(flightRequestRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetAllRequestsByUserId() {
        Long userId = 1L;
        var flightRequestEntity1 = FlightRequestEntity.builder()
                .user(new User(userId, InputDataState.SOURCE))
                .id(1L)
                .build();
        var flightRequestEntity2 = FlightRequestEntity.builder()
                .user(new User(userId, InputDataState.SOURCE))
                .id(2L)
                .build();
        List<FlightRequestEntity> mockRequests = List.of(flightRequestEntity1, flightRequestEntity2);
        when(flightRequestRepository.findByUserId(userId)).thenReturn(mockRequests);

        var result = flightRequestService.getAllRequestsByUserId(0, 1, userId);

        assertEquals(2, result.totalPageNumber());
        assertEquals(1, result.inlineKeyboardButtonInfoList().size());
        verify(flightRequestRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testSaveFlightRequest() {
        FlightRequest flightRequest = FlightRequest.builder()
                .action(Action.POST)
                .userId("1")
                .fromPlace("NYC")
                .toPlace("LAX")
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(7))
                .currency("USD")
                .price(BigDecimal.valueOf(200))
                .build();

        FlightRequestEntity mockEntity = new FlightRequestEntity();
        when(conversionService.convert(flightRequest, FlightRequestEntity.class)).thenReturn(mockEntity);
        when(userService.getUser(1L)).thenReturn(null);

        flightRequestService.saveFlightRequest(flightRequest);

        verify(queryProducer, times(1)).sendUpdate(flightRequest);
        verify(flightRequestRepository, times(1)).save(mockEntity);
    }

    @Test
    void testRemoveFlightRequest() {
        Long requestId = 1L;
        FlightRequestEntity mockEntity = new FlightRequestEntity();
        when(flightRequestRepository.findById(requestId)).thenReturn(Optional.of(mockEntity));
        FlightRequest mockFlightRequest = new FlightRequest();
        when(conversionService.convert(mockEntity, FlightRequest.class)).thenReturn(mockFlightRequest);

        flightRequestService.removeFlightRequest(requestId);

        verify(flightRequestRepository, times(1)).deleteById(requestId);
        verify(queryProducer, times(1)).sendUpdate(mockFlightRequest);
        assertEquals(Action.DELETE, mockFlightRequest.getAction());
    }

    @Test
    void testRemoveFlightRequest_NotFound() {
        Long requestId = 1L;
        when(flightRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightRequestService.removeFlightRequest(requestId));

        assertEquals("No flight request found", exception.getMessage());
        verify(flightRequestRepository, times(1)).findById(requestId);
        verify(flightRequestRepository, never()).deleteById(anyLong());
        verify(queryProducer, never()).sendUpdate(any());
    }
}
