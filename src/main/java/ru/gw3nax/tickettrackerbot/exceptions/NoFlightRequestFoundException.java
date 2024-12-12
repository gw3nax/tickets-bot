package ru.gw3nax.tickettrackerbot.exceptions;

public class NoFlightRequestFoundException extends RuntimeException {
    public NoFlightRequestFoundException(String noFlightRequestFound) {
        super(noFlightRequestFound);
    }
}
