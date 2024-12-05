package ru.gw3nax.tickettrackerbot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gw3nax.tickettrackerbot.command.Command;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final List<Command> commands;
    private final InputDataStateHandler inputDataStateHandler;
    private final MessageCallbackQueryHandler messageCallbackQueryHandler;

    public SendMessage handle(Update update) {
        Optional<Command> command = commands.stream()
                .filter(e -> e.supports(update))
                .findFirst();

        if (command.isPresent()) {
            return command.get().handle(update);
        }
        try {
            return inputDataStateHandler.handleState(update);
        } catch (Exception e) {
            return new SendMessage(update.message().from().id(), "Что-то пошло не так.");
        }
    }
}
