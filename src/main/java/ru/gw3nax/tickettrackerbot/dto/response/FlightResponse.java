package ru.gw3nax.tickettrackerbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightResponse {
    private String userId;
    private List<FlightResponseData> data;
}

