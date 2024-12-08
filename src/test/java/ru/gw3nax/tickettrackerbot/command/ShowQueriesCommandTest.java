package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ShowQueriesCommandTest {

    private FlightRequestService flightRequestService;
    private ShowQueriesCommand showQueriesCommand;

    @BeforeEach
    void setUp() {
        flightRequestService = mock(FlightRequestService.class);
        showQueriesCommand = new ShowQueriesCommand(flightRequestService);
    }

    @Test
    void command_shouldReturnCorrectCommand() {
        assertEquals("/show_queries", showQueriesCommand.command());
    }

    @Test
    void description_shouldReturnCorrectDescription() {
        assertEquals("Команда для получения информации о запросах на поиск билета", showQueriesCommand.description());
    }

    @Test
    void handle_shouldReturnMessageIfNoRequestsFound() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        when(flightRequestService.findAllRequestsByUserId(12345L)).thenReturn(List.of());

        SendMessage response = showQueriesCommand.handle(update);

        verify(flightRequestService).findAllRequestsByUserId(12345L);

        assertNotNull(response);
        assertEquals(12345L, response.getParameters().get("chat_id"));
        assertEquals("В данный момент вы не ищите никакие билеты", response.getParameters().get("text"));
    }

    @Test
    void handle_shouldReturnFormattedMessageIfRequestsFound() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        com.pengrad.telegrambot.model.Chat chat = mock(com.pengrad.telegrambot.model.Chat.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);
        when(message.chat()).thenReturn(chat);

        var request1 = FlightRequestEntity.builder()
                .fromPlace("LED")
                .toPlace("SVO")
                .fromDate(LocalDate.of(2024, 12, 12))
                .toDate(LocalDate.of(2024, 12, 13))
                .currency("RUB")
                .price(BigDecimal.valueOf(5000))
                .build();
        var request2 = FlightRequestEntity.builder()
                .fromPlace("LED")
                .toPlace("VVO")
                .fromDate(LocalDate.of(2024, 12, 12))
                .toDate(LocalDate.of(2024, 12, 13))
                .currency("RUB")
                .price(BigDecimal.valueOf(30000))
                .build();

        when(flightRequestService.findAllRequestsByUserId(12345L)).thenReturn(List.of(request1, request2));

        SendMessage response = showQueriesCommand.handle(update);

        verify(flightRequestService).findAllRequestsByUserId(12345L);

        assertNotNull(response);
        assertEquals(12345L, response.getParameters().get("chat_id"));

        String expectedText = """
            1. LED -> SVO
            2024-12-12 -> 2024-12-13
            Цена: 5000 RUB
            
            2. LED -> VVO
            2024-12-12 -> 2024-12-13
            Цена: 30000 RUB
            
            """;
        assertEquals(expectedText, response.getParameters().get("text"));
    }

}
