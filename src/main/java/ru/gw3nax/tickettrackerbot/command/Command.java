package ru.gw3nax.tickettrackerbot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {

    String command();

    String description();

    SendMessage handle(Update update);

    default boolean supports(Update update) {
        if (update.message() == null) {
            return false;
        } else if (update.message().text() == null) {
            return false;
        } else {
            return update
                    .message()
                    .text()
                    .strip()
                    .split(" ", 2)[0]
                    .equals(command());
        }
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}