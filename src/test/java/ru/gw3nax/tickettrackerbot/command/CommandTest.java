package ru.gw3nax.tickettrackerbot.command;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandTest {

    private final Command testCommand = new Command() {
        @Override
        public String command() {
            return "/test";
        }

        @Override
        public String description() {
            return "Test command description";
        }

        @Override
        public SendMessage handle(Update update) {
            return null;
        }
    };

    @Test
    void supports_shouldReturnFalseIfMessageIsNull() {
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        assertFalse(testCommand.supports(update));
    }

    @Test
    void supports_shouldReturnFalseIfMessageTextIsNull() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(null);

        assertFalse(testCommand.supports(update));
    }

    @Test
    void supports_shouldReturnFalseIfTextDoesNotMatchCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/wrongCommand");

        assertFalse(testCommand.supports(update));
    }

    @Test
    void supports_shouldReturnTrueIfTextMatchesCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/test");

        assertTrue(testCommand.supports(update));
    }

    @Test
    void supports_shouldReturnTrueIfTextMatchesCommandWithExtraWhitespace() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(" /test   ");

        assertTrue(testCommand.supports(update));
    }

    @Test
    void toApiCommand_shouldReturnCorrectBotCommand() {
        BotCommand botCommand = testCommand.toApiCommand();

        assertNotNull(botCommand);
        assertEquals("/test", botCommand.command());
        assertEquals("Test command description", botCommand.description());
    }
}
