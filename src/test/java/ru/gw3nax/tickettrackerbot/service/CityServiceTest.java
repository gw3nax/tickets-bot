package ru.gw3nax.tickettrackerbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CityServiceTest {

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        var testCitiesJson = getClass().getResourceAsStream("/static/cities.json");
        if (testCitiesJson == null) {
            throw new RuntimeException("Test cities.json not found!");
        }

        cityService.loadCities();
    }

    @Test
    public void testGetIataCode() {
        String iataCode = cityService.getIataCode("физули");
        assertEquals("FZL", iataCode);

        String iataCodeUpperCase = cityService.getIataCode("Физули");
        assertEquals("FZL", iataCodeUpperCase);

        String unknownCityCode = cityService.getIataCode("UnknownCity");
        assertNull(unknownCityCode);
    }
}