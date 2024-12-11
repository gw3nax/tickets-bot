package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final UserService userService;
    private static final String DESCRIPTION = "Команда для регистрации пользователя в боте";
    private static final String COMMAND = "/start";
    private static final String TEXT = """
            Добро пожаловать!
            Я - телеграм бот для поиска дешевых авиабилетов
            
            Вы можете взаимодействовать со мной с помощью следующих команд:
          
            1. /start - Команда для регистрации пользователя в боте
            2. /search - Команда для поиска авиабилетов
            3. /remove_query - Команда для удаления запроса на поиск авиабилета
            4. /help - Команда для вывода информации о боте
            5. /show_queries - Команда для получения информации о запросах на поиск билета
            """;


    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        log.info("Start command executed");
        userService.registerUser(update.message().from().id());
        return new SendMessage(update.message().from().id(), TEXT);
    }
}
