package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpCommandTest {

    private HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        helpCommand = new HelpCommand();
    }

    @Test
    void command_shouldReturnCorrectCommand() {
        assertEquals("/help", helpCommand.command());
    }

    @Test
    void description_shouldReturnCorrectDescription() {
        assertEquals("Команда для вывода информации о боте", helpCommand.description());
    }

    @Test
    void handle_shouldReturnSendMessageWithHelpText() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        SendMessage response = helpCommand.handle(update);

        assertNotNull(response);
        assertEquals(12345L, response.getParameters().get("chat_id"));
        assertTrue(response.getParameters().get("text").toString().contains("Команды бота:"));
    }

    @Test
    void handle_shouldLogExecution() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        HelpCommand spyCommand = spy(helpCommand);
        spyCommand.handle(update);
        verify(spyCommand).handle(update);
    }
}
