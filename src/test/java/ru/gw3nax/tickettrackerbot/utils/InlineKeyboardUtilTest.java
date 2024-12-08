package ru.gw3nax.tickettrackerbot.utils;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import org.junit.jupiter.api.Test;
import ru.gw3nax.tickettrackerbot.model.InlineKeyboardButtonInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InlineKeyboardUtilTest {

    @Test
    void createKeyboard_shouldReturnCorrectMarkupForSinglePage() {
        List<InlineKeyboardButtonInfo> buttonInfoList = List.of(
                new InlineKeyboardButtonInfo("Button 1", "1"),
                new InlineKeyboardButtonInfo("Button 2", "2")
        );

        InlineKeyboardMarkup markup = InlineKeyboardUtil.createKeyboard(buttonInfoList, 0, 1);

        assertNotNull(markup);
        assertEquals(3, markup.inlineKeyboard().length);
        assertEquals("Button 1", markup.inlineKeyboard()[0][0].text());
        assertEquals("1", markup.inlineKeyboard()[0][0].callbackData());
        assertEquals("Button 2", markup.inlineKeyboard()[1][0].text());
        assertEquals("2", markup.inlineKeyboard()[1][0].callbackData());
    }

    @Test
    void createKeyboard_shouldIncludeNavigationButtons() {
        List<InlineKeyboardButtonInfo> buttonInfoList = List.of(
                new InlineKeyboardButtonInfo("Button 1", "1")
        );

        InlineKeyboardMarkup markup = InlineKeyboardUtil.createKeyboard(buttonInfoList, 0, 3);

        assertNotNull(markup);
        assertEquals(2, markup.inlineKeyboard().length);
        assertEquals(">", markup.inlineKeyboard()[1][0].text());
        assertEquals("right/1", markup.inlineKeyboard()[1][0].callbackData());
    }

    @Test
    void createKeyboard_shouldIncludeLeftAndRightButtonsForMiddlePage() {
        List<InlineKeyboardButtonInfo> buttonInfoList = List.of(
                new InlineKeyboardButtonInfo("Button 1", "1")
        );

        InlineKeyboardMarkup markup = InlineKeyboardUtil.createKeyboard(buttonInfoList, 1, 3);

        assertNotNull(markup);
        assertEquals(2, markup.inlineKeyboard().length);
        assertEquals("<", markup.inlineKeyboard()[1][0].text());
        assertEquals("left/0", markup.inlineKeyboard()[1][0].callbackData());
        assertEquals(">", markup.inlineKeyboard()[1][1].text());
        assertEquals("right/2", markup.inlineKeyboard()[1][1].callbackData());
    }

    @Test
    void updateMessage_shouldReturnCorrectEditMessageReplyMarkup() {
        List<InlineKeyboardButtonInfo> buttonInfoList = List.of(
                new InlineKeyboardButtonInfo("Button 1", "1")
        );

        EditMessageReplyMarkup editMessage = InlineKeyboardUtil.updateMessage(123L, 456, "right/1", buttonInfoList, 3);

        assertNotNull(editMessage);
        assertEquals(123L, editMessage.getParameters().get("chat_id"));
        assertEquals(456, editMessage.getParameters().get("message_id"));
        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) editMessage.getParameters().get("reply_markup");
        assertNotNull(markup);

        assertEquals(2, markup.inlineKeyboard().length);
        assertEquals("Button 1", markup.inlineKeyboard()[0][0].text());
        assertEquals("1", markup.inlineKeyboard()[0][0].callbackData());
        assertEquals(">", markup.inlineKeyboard()[1][1].text());
        assertEquals("right/2", markup.inlineKeyboard()[1][1].callbackData());
    }
}
