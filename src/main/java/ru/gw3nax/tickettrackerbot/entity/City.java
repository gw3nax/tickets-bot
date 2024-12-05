package ru.gw3nax.tickettrackerbot.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {
    @JsonProperty("cases")
    private Map<String, String> cases;
    private String name;
    private String code;
}
