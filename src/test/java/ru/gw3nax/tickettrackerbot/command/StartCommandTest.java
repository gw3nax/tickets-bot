package ru.gw3nax.tickettrackerbot.command;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.gw3nax.tickettrackerbot.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartCommandTest {

    private UserService userService;
    private StartCommand startCommand;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        startCommand = new StartCommand(userService);
    }

    @Test
    void command_shouldReturnCorrectCommand() {
        assertEquals("/start", startCommand.command());
    }

    @Test
    void description_shouldReturnCorrectDescription() {
        assertEquals("Команда для регистрации пользователя в боте", startCommand.description());
    }

    @Test
    void handle_shouldRegisterUserAndReturnWelcomeMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(12345L);

        SendMessage response = startCommand.handle(update);

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userService).registerUser(userIdCaptor.capture());
        assertEquals(12345L, userIdCaptor.getValue());

        assertNotNull(response);
        assertEquals(12345L, response.getParameters().get("chat_id"));

        String expectedText = """
                Добро пожаловать!
                Я - телеграм бот для поиска дешевых авиабилетов
                
                Вы можете взаимодействовать со мной с помощью следующих команд:
              
                1. /start - Команда для регистрации пользователя в боте
                2. /search - Команда для поиска авиабилетов
                3. /remove_query - Команда для удаления запроса на поиск авиабилета
                4. /help - Команда для вывода информации о боте
                5. /show_queries - Команда для получения информации о запросах на поиск билета
                """;

        assertEquals(expectedText, response.getParameters().get("text"));
    }
}
