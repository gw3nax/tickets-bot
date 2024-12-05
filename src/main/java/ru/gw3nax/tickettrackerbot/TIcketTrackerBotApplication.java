package ru.gw3nax.tickettrackerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.gw3nax.tickettrackerbot.configuration.properties.ApplicationConfig;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaConsumerProperties;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaProducerProperties;

@SpringBootApplication
@EnableConfigurationProperties({KafkaConsumerProperties.class, KafkaProducerProperties.class, ApplicationConfig.class})
public class TIcketTrackerBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TIcketTrackerBotApplication.class, args);
    }

}
