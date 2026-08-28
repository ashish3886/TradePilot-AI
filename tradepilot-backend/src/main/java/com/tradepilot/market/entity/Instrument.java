package com.tradepilot.market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "instruments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_instrument_symbol",
            columnNames = "symbol"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String exchange;

    @Column(nullable = false, length = 20)
    private String instrumentType;

    @Column(nullable = false)
    private boolean active;
}