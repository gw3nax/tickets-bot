package ru.gw3nax.tickettrackerbot.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CallbackQueryParser {

    public Long getRequestId(String string) {
        try {
            return Long.valueOf(string);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getArrowPageNumber(String string) {
        try {
            String[] parts = string.split("/");
            if (parts.length < 2) {
                return -1;
            }
            return Integer.valueOf(parts[1]);
        } catch (Exception e) {
            return -1;
        }
    }
}
