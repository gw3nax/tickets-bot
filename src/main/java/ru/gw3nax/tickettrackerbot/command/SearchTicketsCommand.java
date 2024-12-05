package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchTicketsCommand implements Command {

    private static final String DESCRIPTION = "Команда для поиска авиабилетов";
    private static final String COMMAND = "/search";

    private final Map<Long, InputDataState> stateMap;

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
        Long userId = update.message().from().id();
        stateMap.put(userId, InputDataState.SOURCE);
        return new SendMessage(userId, "Выберите пункт отправления.");
    }
}
