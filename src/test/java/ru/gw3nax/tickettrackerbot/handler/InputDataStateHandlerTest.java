package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.service.CityService;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;
import ru.gw3nax.tickettrackerbot.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InputDataStateHandlerTest {

    private UserService userService;
    private CityService cityService;
    private FlightRequestService flightRequestService;
    private InputDataStateHandler stateHandler;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        cityService = mock(CityService.class);
        flightRequestService = mock(FlightRequestService.class);
        stateHandler = new InputDataStateHandler(userService, cityService, flightRequestService);
    }

    private Update createMockUpdate(Long userId, String messageText) {
        var update = mock(Update.class);
        var message = mock(Message.class);
        var user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(userId);
        when(message.text()).thenReturn(messageText);
        return update;
    }

    @Test
    void handleState_shouldHandleSourceState() {
        var userId = 12345L;
        var update = createMockUpdate(userId, "Москва");

        when(userService.getState(userId)).thenReturn(InputDataState.SOURCE);
        when(cityService.getIataCode("Москва")).thenReturn("MOW");

        var response = stateHandler.handleState(update);

        verify(userService).updateState(userId, InputDataState.DESTINATION);
        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Введите пункт назначения", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleInvalidSource() {
        var userId = 12345L;
        var invalidData = "invalid-data";
        var update = createMockUpdate(userId, invalidData);

        when(userService.getState(userId)).thenReturn(InputDataState.SOURCE);
        when(cityService.getIataCode(invalidData)).thenReturn(null);

        var response = stateHandler.handleState(update);
        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Город не найден. Попробуйте снова", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleDestinationState() {
        var userId = 12345L;
        var update = createMockUpdate(userId, "Сочи");

        when(userService.getState(userId)).thenReturn(InputDataState.DESTINATION);
        when(cityService.getIataCode("Сочи")).thenReturn("AER");

        var response = stateHandler.handleState(update);

        verify(userService).updateState(userId, InputDataState.DEPARTURE_DATE_FROM);
        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Введите начальную дату окна вылета (yyyy-MM-dd)", response.getParameters().get("text"));
    }
    @Test
    void handleState_shouldHandleInvalidDestination() {
        var userId = 12345L;
        var invalidData = "invalid-data";
        var update = createMockUpdate(userId, invalidData);

        when(userService.getState(userId)).thenReturn(InputDataState.DESTINATION);
        when(cityService.getIataCode(invalidData)).thenReturn(null);

        var response = stateHandler.handleState(update);

        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Город не найден. Попробуйте снова", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleDepartureDateFromState() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, "2024-12-12");

        when(userService.getState(userId)).thenReturn(InputDataState.DEPARTURE_DATE_FROM);

        SendMessage response = stateHandler.handleState(update);

        verify(userService).updateState(userId, InputDataState.DEPARTURE_DATE_TO);
        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Введите конечную дату окна вылета (yyyy-MM-dd)", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandlePriceState() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, "5000");

        when(userService.getState(userId)).thenReturn(InputDataState.PRICE);

        SendMessage response = stateHandler.handleState(update);

        verify(flightRequestService).saveFlightRequest(any(FlightRequest.class));
        verify(userService).clearState(userId);
        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Ваш запрос отправлен", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleInvalidDate() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, "invalid-date");

        when(userService.getState(userId)).thenReturn(InputDataState.DEPARTURE_DATE_FROM);

        SendMessage response = stateHandler.handleState(update);

        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Неверный формат даты. Попробуйте снова", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleInvalidDate_isBeforeCurrentDate() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, LocalDate.now().minusDays(1).toString());

        when(userService.getState(userId)).thenReturn(InputDataState.DEPARTURE_DATE_FROM);

        SendMessage response = stateHandler.handleState(update);

        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Дата должна быть не ранее сегодняшнего дня.\nПопробуйте еще раз!", response.getParameters().get("text"));
    }

    @Test
    void handleState_shouldHandleInvalidDateTo() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, "invalid-date");

        when(userService.getState(userId)).thenReturn(InputDataState.DEPARTURE_DATE_TO);

        SendMessage response = stateHandler.handleState(update);

        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Неверный формат даты. Попробуйте снова", response.getParameters().get("text"));
    }



    @Test
    void handleState_shouldHandleInvalidPrice() {
        Long userId = 12345L;
        Update update = createMockUpdate(userId, "invalid-price");

        when(userService.getState(userId)).thenReturn(InputDataState.PRICE);

        SendMessage response = stateHandler.handleState(update);

        assertEquals(userId, response.getParameters().get("chat_id"));
        assertEquals("Цена указана в неправильном формате. Введите ее еще раз.", response.getParameters().get("text"));
    }


}
