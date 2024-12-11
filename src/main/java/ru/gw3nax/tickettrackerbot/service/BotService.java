package ru.gw3nax.tickettrackerbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.dto.response.FlightResponse;
import ru.gw3nax.tickettrackerbot.repository.FlightRequestRepository;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot telegramBot;
    private final FlightRequestRepository flightRequestRepository;

    public void update(FlightResponse flightResponse) {
        var userId = flightResponse.getUserId();
        var builder = new StringBuilder();
        builder.append("Найдены билеты по вашему запросу:\n\n");
        var counter = 1;
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        for (var data : flightResponse.getData()) {
            builder.append(counter).append(". ")
                    .append(data.getFromPlace()).append(" -> ").append(data.getToPlace()).append("\n")
                    .append("Дата перелёта: ").append(data.getDepartureAt().toLocalDate()).append(" ").append(data.getDepartureAt().toLocalTime()).append("\n")
                    .append("Авиакомпания: ").append(data.getAirline()).append("\n")
                    .append("Цена: ").append(data.getPrice()).append("\n");
            keyboard.addRow(new InlineKeyboardButton(data.getAirline() + " : " + data.getDepartureAt().toLocalDate() + " " + data.getDepartureAt().toLocalTime())
                    .webApp(new WebAppInfo("https://www.aviasales.ru" + data.getLink())));
            counter++;
        }

        var message = builder.toString();
        telegramBot.execute(
                new SendMessage(userId, message).replyMarkup(keyboard)
        );
    }
}