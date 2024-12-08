package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchTicketsCommandTest {

    private UserService userService;
    private SearchTicketsCommand searchTicketsCommand;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        searchTicketsCommand = new SearchTicketsCommand(userService);
    }

    @Test
    void command_shouldReturnCorrectCommand() {
        assertEquals("/search", searchTicketsCommand.command());
    }

    @Test
    void description_shouldReturnCorrectDescription() {
        assertEquals("Команда для поиска авиабилетов", searchTicketsCommand.description());
    }

    @Test
    void handle_shouldUpdateUserStateAndReturnMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        SendMessage response = searchTicketsCommand.handle(update);

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<InputDataState> stateCaptor = ArgumentCaptor.forClass(InputDataState.class);
        verify(userService).updateState(userIdCaptor.capture(), stateCaptor.capture());

        assertEquals(12345L, userIdCaptor.getValue());
        assertEquals(InputDataState.SOURCE, stateCaptor.getValue());

        assertNotNull(response);
        assertEquals(12345L, response.getParameters().get("chat_id"));
        assertEquals("Выберите пункт отправления.", response.getParameters().get("text"));
    }
}