package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MessageCallbackQueryHandlerTest {

    @Mock
    private FlightRequestService flightRequestService;

    @InjectMocks
    private MessageCallbackQueryHandler messageCallbackQueryHandler;

    private CallbackQuery callbackQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_shouldReturnEditMessageReplyMarkupForArrowLeft() {
        callbackQuery = mock(CallbackQuery.class);
        User mockUser = mock(User.class);
        when(callbackQuery.data()).thenReturn("left/1");
        when(callbackQuery.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);
        when(callbackQuery.maybeInaccessibleMessage()).thenReturn(mock(Message.class));
        when(callbackQuery.maybeInaccessibleMessage().chat()).thenReturn(Mockito.mock(String.valueOf(Message.class)));

        InlineKeyboardInfo inlineKeyboardInfo = mock(InlineKeyboardInfo.class);
        when(flightRequestService.getAllRequestsByUserId(1, 3, 12345L)).thenReturn(inlineKeyboardInfo);

        var request = messageCallbackQueryHandler.handle(callbackQuery);

        assertTrue(request instanceof EditMessageReplyMarkup);
        verify(flightRequestService).getAllRequestsByUserId(1, 3, 12345L);
    }

    @Test
    void handle_shouldReturnSendMessageForInvalidArrowData() {
        callbackQuery = mock(CallbackQuery.class);
        User mockUser = mock(User.class);
        when(callbackQuery.data()).thenReturn("invalidData");
        when(callbackQuery.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);

        var request = messageCallbackQueryHandler.handle(callbackQuery);

        assertTrue(request instanceof SendMessage);
        assertEquals("Ваш запрос удален", request.getParameters().get("text"));
    }

    @Test
    void handle_shouldReturnSendMessageForDeleteRequest() {
        // Prepare test data
        callbackQuery = mock(CallbackQuery.class);
        User mockUser = mock(User.class);
        when(callbackQuery.data()).thenReturn("12345");
        when(callbackQuery.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);

        SendMessage response = (SendMessage) messageCallbackQueryHandler.handle(callbackQuery);

        assertEquals("Ваш запрос удален", response.getParameters().get("text"));

        verify(flightRequestService).removeFlightRequest(12345L);
    }

    @Test
    void handle_shouldReturnErrorMessageWhenExceptionOccursInDelete() {
        callbackQuery = mock(CallbackQuery.class);
        User mockUser = mock(User.class);
        when(callbackQuery.data()).thenReturn("12345");
        when(callbackQuery.from()).thenReturn(mockUser);
        when(mockUser.id()).thenReturn(12345L);

        doThrow(new RuntimeException("Error")).when(flightRequestService).removeFlightRequest(12345L);

        SendMessage response = (SendMessage) messageCallbackQueryHandler.handle(callbackQuery);

        assertEquals("Something went wrong. Please try again later", response.getParameters().get("text"));
    }
}
