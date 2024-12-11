package ru.gw3nax.tickettrackerbot.model;

import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;

import java.util.List;

public record InlineKeyboardInfo(
        Integer totalPageNumber,
        List<InlineKeyboardButtonInfo> inlineKeyboardButtonInfoList
) {

    public static InlineKeyboardButtonInfo getButtonInfo(FlightRequestEntity flightRequestEntity) {
        var builder = new StringBuilder();
        var buttonText = builder.append(flightRequestEntity.getFromPlace()).append(" -> ").append(flightRequestEntity.getToPlace()).append("\n")
                .append(flightRequestEntity.getFromDate()).append(" -> ").append(flightRequestEntity.getToDate()).toString();
        return new InlineKeyboardButtonInfo(buttonText, flightRequestEntity.getId().toString());
    }
}
