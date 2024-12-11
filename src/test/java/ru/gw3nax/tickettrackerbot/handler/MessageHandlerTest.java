package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.gw3nax.tickettrackerbot.command.Command;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageHandlerTest {

    @Mock
    private Command command1;

    @Mock
    private Command command2;

    @Mock
    private InputDataStateHandler inputDataStateHandler;

    @Mock
    private Update update;

    private MessageHandler messageHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new MessageHandler(List.of(command1, command2), inputDataStateHandler, null);
    }

    @Test
    void testHandle_commandSupported() {
        SendMessage expectedResponse = new SendMessage(12345L, "Command handled");
        when(command1.supports(update)).thenReturn(true);
        when(command1.handle(update)).thenReturn(expectedResponse);

        SendMessage actualResponse = messageHandler.handle(update);

        assertEquals(expectedResponse, actualResponse);
        verify(command1, times(1)).supports(update);
        verify(command1, times(1)).handle(update);
        verifyNoInteractions(inputDataStateHandler);
    }

    @Test
    void testHandle_noCommandSupported() {
        SendMessage expectedResponse = new SendMessage(12345L, "Handled by state");
        when(command1.supports(update)).thenReturn(false);
        when(command2.supports(update)).thenReturn(false);
        when(inputDataStateHandler.handleState(update)).thenReturn(expectedResponse);

        SendMessage actualResponse = messageHandler.handle(update);

        assertEquals(expectedResponse, actualResponse);
        verify(command1, times(1)).supports(update);
        verify(command2, times(1)).supports(update);
        verify(inputDataStateHandler, times(1)).handleState(update);
    }
    @Test
    void testHandle_exceptionInStateHandler() {
        Message mockMessage = mock(Message.class);
        com.pengrad.telegrambot.model.User mockUser = mock(com.pengrad.telegrambot.model.User.class);

        when(update.message()).thenReturn(mockMessage);
        when(mockMessage.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);

        when(command1.supports(update)).thenReturn(false);
        when(command2.supports(update)).thenReturn(false);
        when(inputDataStateHandler.handleState(update)).thenThrow(new RuntimeException("Test exception"));

        SendMessage expectedResponse = new SendMessage(12345L, "Что-то пошло не так.");

        SendMessage actualResponse = messageHandler.handle(update);

        assertEquals(expectedResponse.getParameters(), actualResponse.getParameters());
        verify(inputDataStateHandler, times(1)).handleState(update);
    }

}
