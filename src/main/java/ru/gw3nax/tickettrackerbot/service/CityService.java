package ru.gw3nax.tickettrackerbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.model.City;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CityService {

    private final Map<String, City> citiesByName = new HashMap<>();

    @PostConstruct
    public void loadCities() {
        try (InputStream input = getClass().getResourceAsStream("/static/cities.json")) {
            if (input == null) {
                throw new IOException("cities.json not found in resources/static");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<City> cities = objectMapper.readValue(input, new TypeReference<>() {
            });

            for (City city : cities) {
                var cases = city.getCases().values();
                String nameRu = city.getName().toLowerCase();
                cases.forEach(item -> citiesByName.put(item, city));
                citiesByName.put(nameRu, city);
            }
            log.info("Loaded {} cities into memory", cities.size());
        } catch (IOException e) {
            log.error("Failed to load cities.json", e);
        }
    }

    public String getIataCode(String cityName) {
        City city = citiesByName.get(cityName.toLowerCase());
        return city != null ? city.getCode() : null;
    }
}
