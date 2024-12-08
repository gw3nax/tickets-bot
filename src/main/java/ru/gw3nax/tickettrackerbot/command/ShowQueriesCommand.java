package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowQueriesCommand implements Command {

    private static final String DESCRIPTION = "Команда для получения информации о запросах на поиск билета";
    private static final String COMMAND = "/show_queries";
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
        var requests = flightRequestService.findAllRequestsByUserId(userId);
        var builder = new StringBuilder();
        var counter = 1;
        if (requests.isEmpty()) {
            return new SendMessage(userId, "В данный момент вы не ищите никакие билеты");
        }
        for (var request : requests) {
            builder.append(counter).append(". ")
                    .append(request.getFromPlace()).append(" -> ").append(request.getToPlace()).append("\n")
                    .append(request.getFromDate()).append(" -> ").append(request.getToDate()).append("\n")
                    .append("Цена: ").append(request.getPrice()).append(" ").append(request.getCurrency()).append("\n\n");
            counter++;
        }

        return new SendMessage(userId, builder.toString());
    }
}
