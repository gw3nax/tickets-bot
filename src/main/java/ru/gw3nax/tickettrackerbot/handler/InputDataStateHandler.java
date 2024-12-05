package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.dto.request.Action;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.service.CityService;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InputDataStateHandler {

    private final Map<Long, InputDataState> inputDataStateMap;
    private final Map<Long, FlightRequest.FlightRequestBuilder> flightRequestBuilders = new HashMap<>();
    private final CityService cityService;
    private final FlightRequestService flightRequestService;

    public SendMessage handleState(Update update) {
        var userId = update.message().from().id();
        var userMessage = update.message().text();
        InputDataState currentState = inputDataStateMap.get(userId);

        FlightRequest.FlightRequestBuilder builder = flightRequestBuilders.computeIfAbsent(userId, k -> FlightRequest.builder());
        builder.userId(String.valueOf(userId));
        switch (currentState) {
            case SOURCE:
                String fromIata = cityService.getIataCode(userMessage);
                if (fromIata == null) {
                    return new SendMessage(userId, "Город не найден. Попробуйте снова");
                }
                builder.fromPlace(fromIata);
                inputDataStateMap.put(userId, InputDataState.DESTINATION);
                return new SendMessage(userId, "Введите пункт назначения");

            case DESTINATION:
                String toIata = cityService.getIataCode(userMessage);
                if (toIata == null) {
                    return new SendMessage(userId, "Город не найден. Попробуйте снова");
                }
                builder.toPlace(toIata);
                inputDataStateMap.put(userId, InputDataState.DEPARTURE_DATE_FROM);
                return new SendMessage(userId, "Введите дату вылета (yyyy-MM-dd)");

            case DEPARTURE_DATE_FROM:
                try {
                    if (LocalDate.parse(userMessage).isAfter(LocalDate.now().minusDays(1))) {
                        builder.fromDate(LocalDate.parse(userMessage));
                        inputDataStateMap.put(userId, InputDataState.DEPARTURE_DATE_TO);
                        return new SendMessage(userId, "Введите дату возвращения (yyyy-MM-dd)");
                    } else {
                        return new SendMessage(userId, "Дата должна быть не ранее сегодняшнего дня.\nПопробуйте еще раз!");
                    }

                } catch (DateTimeParseException e) {
                    return new SendMessage(userId, "Неверный формат даты. Попробуйте снова");
                }

            case DEPARTURE_DATE_TO:
                try {
                    if (LocalDate.parse(userMessage).isAfter(builder.build().getFromDate().minusDays(1))) {
                        builder.toDate(LocalDate.parse(userMessage));
                        inputDataStateMap.put(userId, InputDataState.CURRENCY);
                        return new SendMessage(userId, "Введите валюту (например, RUB)");
                    } else {
                        return new SendMessage(userId, "Дата должна быть не позднее даты вылета.\nПопробуйте еще раз!");
                    }
                } catch (DateTimeParseException e) {
                    return new SendMessage(userId, "Неверный формат даты. Попробуйте снова");
                }


            case CURRENCY:
                builder.currency(userMessage);
                inputDataStateMap.put(userId, InputDataState.PRICE);
                return new SendMessage(userId, "Введите максимальную цену");

            case PRICE:
                try {
                    builder.price(new BigDecimal(userMessage));
                    FlightRequest request = builder.build();
                    request.setAction(Action.POST);
                    flightRequestService.saveFlightRequest(request);
                    inputDataStateMap.remove(userId);
                    flightRequestBuilders.remove(userId);
                    return new SendMessage(userId, "Ваш запрос успешно создан");
                } catch (NumberFormatException e) {
                    return new SendMessage(userId, "Цена указана в неправильном формате. Введите ее еще раз.");
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            default:
                return new SendMessage(userId, "Произошла ошибка. Попробуйте еще раз.");
        }
    }
}

