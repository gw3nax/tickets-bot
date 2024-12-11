package ru.gw3nax.tickettrackerbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallbackQueryParserTest {

    @Test
    void getRequestId_shouldReturnValidId() {
        String input = "12345";
        Long result = CallbackQueryParser.getRequestId(input);
        assertNotNull(result);
        assertEquals(12345L, result);
    }

    @Test
    void getRequestId_shouldReturnNullForInvalidInput() {
        String input = "invalid";
        Long result = CallbackQueryParser.getRequestId(input);
        assertNull(result);
    }

    @Test
    void getRequestId_shouldReturnNullForEmptyString() {
        String input = "";
        Long result = CallbackQueryParser.getRequestId(input);
        assertNull(result);
    }

    @Test
    void getArrowPageNumber_shouldReturnValidPageNumber() {
        String input = "arrow/3";
        Integer result = CallbackQueryParser.getArrowPageNumber(input);
        assertNotNull(result);
        assertEquals(3, result);
    }

    @Test
    void getArrowPageNumber_shouldReturnNegativeOneForInvalidFormat() {
        String input = "invalid";
        Integer result = CallbackQueryParser.getArrowPageNumber(input);
        assertEquals(-1, result);
    }

    @Test
    void getArrowPageNumber_shouldReturnNegativeOneForEmptyString() {
        String input = "";
        Integer result = CallbackQueryParser.getArrowPageNumber(input);
        assertEquals(-1, result);
    }

    @Test
    void getArrowPageNumber_shouldReturnNegativeOneForMissingPageNumber() {
        String input = "arrow/";
        Integer result = CallbackQueryParser.getArrowPageNumber(input);
        assertEquals(-1, result);
    }

    @Test
    void getArrowPageNumber_shouldHandleExtraSlashesGracefully() {
        String input = "arrow/2/";
        Integer result = CallbackQueryParser.getArrowPageNumber(input);
        assertEquals(2, result);
    }
}
