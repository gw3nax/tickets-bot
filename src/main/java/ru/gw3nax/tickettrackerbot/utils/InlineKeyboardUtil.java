package ru.gw3nax.tickettrackerbot.utils;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import lombok.experimental.UtilityClass;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardButtonInfo;

import java.util.List;

@UtilityClass
public class InlineKeyboardUtil {

    public InlineKeyboardMarkup createKeyboard(List<InlineKeyboardButtonInfo> buttonInfoList, int page, int lastPage) {
        var keyboardMarkup = new InlineKeyboardMarkup();
        buttonInfoList.stream()
                .map(item -> new InlineKeyboardButton(item.buttonText())
                        .callbackData(String.join("/", item.flightRequestId())))
                .forEach(keyboardMarkup::addRow);
        return keyboardMarkup.addRow(arrowKeyboard(page, lastPage));
    }

    private InlineKeyboardButton[] arrowKeyboard(int page, int lastPage) {
        var rightCallBack = String.join("/", "right", String.valueOf(page + 1));
        var leftCallBack = String.join("/", "left", String.valueOf(page - 1));
        if (lastPage - 1 == 0) {
            return new InlineKeyboardButton[]{};
        } else if (page == 0) {
            return new InlineKeyboardButton[]{new InlineKeyboardButton(">").callbackData(rightCallBack)};
        } else if (page == lastPage - 1) {
            return new InlineKeyboardButton[]{new InlineKeyboardButton("<").callbackData(leftCallBack)};
        } else {
            return new InlineKeyboardButton[]{
                    new InlineKeyboardButton("<").callbackData(leftCallBack),
                    new InlineKeyboardButton(">").callbackData(rightCallBack)
            };
        }
    }

    public EditMessageReplyMarkup updateMessage(Long chatId, Integer messageId, String data, List<InlineKeyboardButtonInfo> buttonInfoList, int lastPage) {
        var editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, messageId);
        var split = data.split("/");
        int page = Integer.parseInt(split[1]);
        return editMessageReplyMarkup.replyMarkup(
                createKeyboard(buttonInfoList, page, lastPage)
        );
    }
}
