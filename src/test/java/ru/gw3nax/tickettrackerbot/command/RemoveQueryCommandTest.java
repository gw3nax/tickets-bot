package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardButtonInfo;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RemoveQueryCommandTest {

    private FlightRequestService flightRequestService;
    private RemoveQueryCommand removeQueryCommand;

    @BeforeEach
    void setUp() {
        flightRequestService = mock(FlightRequestService.class);
        removeQueryCommand = new RemoveQueryCommand(flightRequestService);
    }

    @Test
    void command_shouldReturnCorrectCommand() {
        assertEquals("/remove_query", removeQueryCommand.command());
    }

    @Test
    void description_shouldReturnCorrectDescription() {
        assertEquals("Команда для удаления текущего запроса", removeQueryCommand.description());
    }

    @Test
    void handle_shouldReturnMessageIfNoRequests() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        when(flightRequestService.getAllRequestsByUserId(0, 3, 12345L)).thenReturn(new InlineKeyboardInfo(0, List.of()));

        SendMessage response = removeQueryCommand.handle(update);

        assertNotNull(response);
        assertEquals("Вы не ищите ни один билет.\nУдалять нечего.", response.getParameters().get("text"));
        assertEquals(12345L, response.getParameters().get("chat_id"));
    }

    @Test
    void handle_shouldReturnMessageWithKeyboardIfRequestsExist() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(chat.id()).thenReturn(12345L);
        when(user.id()).thenReturn(12345L);

        var buttonList = List.of(
                new InlineKeyboardButtonInfo("text1", "12345"),
                new InlineKeyboardButtonInfo("text2", "12345")
        );

        var inlineKeyboardInfo = new InlineKeyboardInfo(2, buttonList);
        when(flightRequestService.getAllRequestsByUserId(0, 3, 12345L)).thenReturn(inlineKeyboardInfo);

        SendMessage response = removeQueryCommand.handle(update);

        assertNotNull(response);
        assertEquals("Какой запрос вы хотите удалить? Пожалуйста, укажите его.", response.getParameters().get("text"));
        assertEquals(12345L, response.getParameters().get("chat_id"));
    }

}
