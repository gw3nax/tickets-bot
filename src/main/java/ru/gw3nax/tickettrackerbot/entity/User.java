package ru.gw3nax.tickettrackerbot.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;

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
}
