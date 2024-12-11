package ru.gw3nax.tickettrackerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gw3nax.tickettrackerbot.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
