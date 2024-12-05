package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.network.Send;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardButtonInfo;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardInfo;
import ru.gw3nax.tickettrackerbot.service.FlightRequestService;
import ru.gw3nax.tickettrackerbot.utils.CallbackQueryParser;
import ru.gw3nax.tickettrackerbot.utils.InlineKeyboardUtil;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageCallbackQueryHandler {
    private static final Integer PAGE_SIZE = 3;
    private final FlightRequestService flightRequestService;

    public BaseRequest<? extends BaseRequest<?,?>,? extends BaseResponse> handle(CallbackQuery callbackQuery) {
        if (callbackQuery.data().contains("left") || callbackQuery.data().contains("right")) {
            EditMessageReplyMarkup result = handleArrow(callbackQuery);
            return Objects.isNull(result) ?
                    new SendMessage(callbackQuery.maybeInaccessibleMessage().chat().id(), "Не удалось обработать запрос") :
                    result;
        } else
            return handleDelete(callbackQuery);
    }

    private EditMessageReplyMarkup handleArrow(CallbackQuery callbackQuery) {
        InlineKeyboardInfo data = getData(callbackQuery);
        return InlineKeyboardUtil.updateMessage(
                callbackQuery.maybeInaccessibleMessage().chat().id(),
                callbackQuery.maybeInaccessibleMessage().messageId(),
                callbackQuery.data(),
                data.inlineKeyboardButtonInfoList(),
                data.totalPageNumber()
        );
    }

    private InlineKeyboardInfo getData(CallbackQuery callbackQuery) {
        // Получаем информацию о запросах пользователя
        var keyboardInfo = flightRequestService.getAllRequestsByUserId(callbackQuery.from().id().toString());

        // Извлекаем номер страницы из callbackData
        var pageNumber = CallbackQueryParser.getArrowPageNumber(callbackQuery.data());

        // Проверка, что pageNumber валидно
        if (pageNumber < 1 || pageNumber > keyboardInfo.totalPageNumber()) {
            // Если номер страницы вне допустимого диапазона, возвращаем первую страницу
            pageNumber = 1;
        }

        // Устанавливаем размер страницы
        int pageSize = 3;
        // Расчитываем индексы для subList
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, keyboardInfo.inlineKeyboardButtonInfoList().size());

        // Проверка, что индексы не выходят за пределы списка
        if (startIndex >= keyboardInfo.inlineKeyboardButtonInfoList().size()) {
            startIndex = 0;
            endIndex = Math.min(pageSize, keyboardInfo.inlineKeyboardButtonInfoList().size());
        }

        // Извлекаем элементы для текущей страницы
        List<InlineKeyboardButtonInfo> pageItems = keyboardInfo.inlineKeyboardButtonInfoList().subList(startIndex, endIndex);

        return new InlineKeyboardInfo(keyboardInfo.totalPageNumber(), pageItems);
    }


    private SendMessage handleDelete(CallbackQuery callbackQuery) {
        //TODO remove
        var userId = callbackQuery.from().id();
        try {
            flightRequestService.removeFlightRequest(CallbackQueryParser.getRequestId(callbackQuery.data()));
            return new SendMessage(userId, "12345678");
        } catch (Exception e) {
            return new SendMessage(userId, "Something went wrong. Please try again later");
        }
    }
}
