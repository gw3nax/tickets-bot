package ru.gw3nax.tickettrackerbot.mapper;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import ru.gw3nax.tickettrackerbot.configuration.MapperConfiguration;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;

@Mapper(config = MapperConfiguration.class)
public interface FlightRequestToFlightRequestEntityMapper extends Converter<FlightRequest, FlightRequestEntity> {

    @Override
    FlightRequestEntity convert(FlightRequest source);
}

