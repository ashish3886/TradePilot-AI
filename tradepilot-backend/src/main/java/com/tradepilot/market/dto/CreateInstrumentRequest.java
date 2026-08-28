package com.tradepilot.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInstrumentRequest(

        @NotBlank
        @Size(max = 50)
        String symbol,

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 30)
        String exchange,

        @NotBlank
        @Size(max = 20)
        String instrumentType
) {
}