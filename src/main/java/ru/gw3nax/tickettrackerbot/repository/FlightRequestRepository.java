package ru.gw3nax.tickettrackerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gw3nax.tickettrackerbot.entity.FlightRequestEntity;

import java.util.List;

@Repository
public interface FlightRequestRepository extends JpaRepository<FlightRequestEntity, Long> {

    List<FlightRequestEntity> findByUserId(String userId);
}
