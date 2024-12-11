package ru.gw3nax.tickettrackerbot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gw3nax.tickettrackerbot.command.Command;
import ru.gw3nax.tickettrackerbot.configuration.properties.ApplicationConfig;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final List<Command> commands;

    @Bean
    public TelegramBot bot(ApplicationConfig applicationConfig) {
        TelegramBot telegramBot = new TelegramBot(applicationConfig.telegramToken());
        createMenu(telegramBot);
        return telegramBot;
    }

    private void createMenu(TelegramBot telegramBot) {
        telegramBot.execute(
                new SetMyCommands(
                        commands.stream()
                                .map(Command::toApiCommand)
                                .collect(Collectors.toList())
                                .toArray(BotCommand[]::new)
                )
        );
    }
}