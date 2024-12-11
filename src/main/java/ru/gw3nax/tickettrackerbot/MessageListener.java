package ru.gw3nax.tickettrackerbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.handler.MessageCallbackQueryHandler;
import ru.gw3nax.tickettrackerbot.handler.MessageHandler;

import java.util.List;

@Component
@Slf4j
public class MessageListener implements UpdatesListener {

    private final MessageCallbackQueryHandler messageCallbackQueryHandler;
    private TelegramBot telegramBot;
    private MessageHandler messageHandler;

    public MessageListener(TelegramBot telegramBot, MessageHandler messageHandler, MessageCallbackQueryHandler messageCallbackQueryHandler) {
        telegramBot.setUpdatesListener(this);

        this.telegramBot = telegramBot;
        this.messageHandler = messageHandler;
        this.messageCallbackQueryHandler = messageCallbackQueryHandler;
    }

    @Override
    public int process(List<Update> list) {

        for (var update : list) {
            if (update != null) {
                if (update.message() != null) {
                    var msg = messageHandler.handle(update);
                    telegramBot.execute(msg);
                }
                if (update.callbackQuery() != null) {
                    telegramBot.execute(messageCallbackQueryHandler.handle(update.callbackQuery()));
                }
            }
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}