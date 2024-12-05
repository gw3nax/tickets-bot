package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;
import ru.gw3nax.tickettrackerbot.service.UserService;
import ru.gw3nax.tickettrackerbot.utils.InlineKeyboardUtil;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveQueryCommand implements Command {

    private static final String DESCRIPTION = "Команда для удаления текущего запроса";
    private static final String COMMAND = "/remove_query";
    private final UserService userService;
    private final FlightRequestService flightRequestService;

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
        var userId = update.message().from().id();
        InlineKeyboardInfo keyboardInfo = flightRequestService.getAllRequestsByUserId(userId);
        if (keyboardInfo == null) {
            return new SendMessage(userId, "Вы не ищите ни один билет.\nУдалять нечего.");
        }
        return new SendMessage(update.message().chat().id(), "Какой запрос вы хотите удалить? Пожалуйста, укажите его.")
                .replyMarkup(InlineKeyboardUtil.createKeyboard(keyboardInfo.inlineKeyboardButtonInfoList(), 0, keyboardInfo.totalPageNumber()));
    }
}
