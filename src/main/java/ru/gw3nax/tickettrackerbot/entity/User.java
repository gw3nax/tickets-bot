package ru.gw3nax.tickettrackerbot.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Nullable
    @Enumerated(value = EnumType.STRING)
    private InputDataState inputDataState;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FlightRequestEntity> flightRequests;
}
