package ru.gw3nax.tickettrackerbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.gw3nax.tickettrackerbot.handler.MessageCallbackQueryHandler;
import ru.gw3nax.tickettrackerbot.handler.MessageHandler;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private MessageHandler messageHandler;

    @Mock
    private MessageCallbackQueryHandler messageCallbackQueryHandler;

    private MessageListener messageListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageListener = new MessageListener(telegramBot, messageHandler, messageCallbackQueryHandler);
    }

    @Test
    void testProcess_withMessage() {
        Update update = mock(Update.class);
        when(update.message()).thenReturn(mock(com.pengrad.telegrambot.model.Message.class));
        SendMessage sendMessage = new SendMessage(123L, "Test message");
        when(messageHandler.handle(update)).thenReturn(sendMessage);

        int result = messageListener.process(List.of(update));

        verify(telegramBot, times(1)).execute(sendMessage);
        verify(messageHandler, times(1)).handle(update);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    @Test
    void testProcess_withNullUpdate() {
        reset(telegramBot);
        int result = messageListener.process(Arrays.asList((Update) null));
        verifyNoMoreInteractions(telegramBot, messageHandler, messageCallbackQueryHandler);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }
}
