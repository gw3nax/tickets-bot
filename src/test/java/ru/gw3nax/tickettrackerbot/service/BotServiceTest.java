package ru.gw3nax.tickettrackerbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gw3nax.tickettrackerbot.dto.response.FlightResponse;
import ru.gw3nax.tickettrackerbot.dto.response.FlightResponseData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class BotServiceTest {
    @Autowired
    private BotService botService;

    @Test
    public void testUpdate() {
        var flight1 = FlightResponseData.builder()
                .fromPlace("Москва")
                .toPlace("Санкт-Петербург")
                .departureAt(LocalDateTime.of(2024, 12, 10, 15, 30))
                .price(BigDecimal.valueOf(5000))
                .airline("SU")
                .link("/link1")
                .build();

        var flight2 = FlightResponseData.builder()
                .fromPlace("Москва")
                .toPlace("Нью-Йорк")
                .departureAt(LocalDateTime.of(2024, 12, 11, 18, 45))
                .price(BigDecimal.valueOf(30000))
                .airline("SU")
                .link("/link2")
                .build();

        FlightResponse flightResponse = FlightResponse.builder()
                .userId("123456789")
                .data(List.of(flight1, flight2))
                .build();

        botService.update(flightResponse);
    }
}