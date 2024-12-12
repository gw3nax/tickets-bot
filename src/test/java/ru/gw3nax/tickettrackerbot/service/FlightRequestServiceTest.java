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
import ru.gw3nax.tickettrackerbot.exceptions.NoFlightRequestFoundException;
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
        User mockUser = new User(null, userId, InputDataState.SOURCE, null);
        List<FlightRequestEntity> mockRequests = List.of(new FlightRequestEntity());

        when(userService.getUser(userId)).thenReturn(mockUser);
        when(flightRequestRepository.findByUserId(mockUser.getId())).thenReturn(mockRequests);

        var result = flightRequestService.findAllRequestsByUserId(userId);

        assertEquals(mockRequests, result);
        verify(userService, times(1)).getUser(userId);
        verify(flightRequestRepository, times(1)).findByUserId(mockUser.getId());
    }


    @Test
    void testGetAllRequestsByUserId() {
        Long userId = 1L;
        User mockUser = new User(null, userId, InputDataState.SOURCE, null);
        var flightRequestEntity1 = FlightRequestEntity.builder().user(mockUser).id(1L).build();
        var flightRequestEntity2 = FlightRequestEntity.builder().user(mockUser).id(2L).build();
        List<FlightRequestEntity> mockRequests = List.of(flightRequestEntity1, flightRequestEntity2);

        when(userService.getUser(userId)).thenReturn(mockUser);
        when(flightRequestRepository.findByUserId(mockUser.getId())).thenReturn(mockRequests);

        var result = flightRequestService.getAllRequestsByUserId(0, 1, userId);

        assertEquals(2, result.totalPageNumber());
        assertEquals(1, result.inlineKeyboardButtonInfoList().size());
        verify(userService, times(1)).getUser(userId);
        verify(flightRequestRepository, times(1)).findByUserId(mockUser.getId());
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
        User mockUser = new User(null, 1L, null, null);

        when(conversionService.convert(flightRequest, FlightRequestEntity.class)).thenReturn(mockEntity);
        when(userService.getUser(1L)).thenReturn(mockUser);

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

        flightRequestService.removeFlightRequest(requestId, 1L);

        verify(flightRequestRepository, times(1)).deleteById(requestId);
        verify(queryProducer, times(1)).sendUpdate(mockFlightRequest);
        assertEquals(Action.DELETE, mockFlightRequest.getAction());
    }

    @Test
    void testRemoveFlightRequest_NotFound() {
        Long requestId = 1L;
        when(flightRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightRequestService.removeFlightRequest(requestId, 1L));

        assertEquals("No flight request found for ID 1", exception.getMessage());
        verify(flightRequestRepository, times(1)).findById(requestId);
        verify(flightRequestRepository, never()).deleteById(anyLong());
        verify(queryProducer, never()).sendUpdate(any());
    }
}
