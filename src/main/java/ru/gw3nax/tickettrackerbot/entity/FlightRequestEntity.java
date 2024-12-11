package ru.gw3nax.tickettrackerbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "flight_requests")
public class FlightRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "from_place", nullable = false)
    private String fromPlace;
    @Column(name = "to_place", nullable = false)
    private String toPlace;
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;
    @Column(name = "to_date")
    private LocalDate toDate;
    @Column(nullable = false)
    private String currency;
    private BigDecimal price;
}
