package ru.gw3nax.tickettrackerbot.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
        @NotEmpty
        String telegramToken
) {
}
