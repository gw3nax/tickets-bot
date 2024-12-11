package ru.gw3nax.tickettrackerbot.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;

import java.util.ArrayList;
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
    private Long id;
    @Nullable
    @Enumerated(value = EnumType.STRING)
    private InputDataState inputDataState;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightRequestEntity> flightRequests = new ArrayList<>();
}
