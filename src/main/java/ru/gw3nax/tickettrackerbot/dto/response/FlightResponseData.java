package ru.gw3nax.tickettrackerbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightResponseData {
    private String fromPlace;
    private String toPlace;
    private LocalDateTime departureAt;
    private BigDecimal price;
    private String airline;
    private String link;
}