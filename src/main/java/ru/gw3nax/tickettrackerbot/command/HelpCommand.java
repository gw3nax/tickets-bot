package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelpCommand implements Command {
    private static final String HELP_TEXT = """
            Команды бота:
          
            1. /start - Команда для регистрации пользователя в боте
            2. /search - Команда для поиска авиабилетов
            3. /remove_query - Команда для удаления запроса на поиск авиабилета
            4. /help - Команда для вывода информации о боте
            5. /show_queries - Команда для получения информации о запросах на поиск билета
            """;
    private static final String DESCRIPTION = "Команда для вывода информации о боте";
    private static final String COMMAND = "/help";

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
        log.info("Help command executed");
        return new SendMessage(update.message().chat().id(), HELP_TEXT);
    }

}
