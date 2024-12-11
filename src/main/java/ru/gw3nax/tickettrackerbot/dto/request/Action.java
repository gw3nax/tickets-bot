package ru.gw3nax.tickettrackerbot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Action {
    POST("post"),
    GET("get"),
    PUT("put"),
    DELETE("delete");
    private final String value;

    @JsonCreator
    public static Action fromValue(String value) {
        for (Action action : Action.values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown action: " + value);
    }
}
