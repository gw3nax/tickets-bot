package ru.gw3nax.tickettrackerbot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StateConfiguration {

    @Bean
    public Map<Long, InputDataState> inputDataStateMap() {
        return new HashMap<>();
    }
}
