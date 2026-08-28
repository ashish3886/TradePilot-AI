package com.tradepilot.market.controller;

import com.tradepilot.market.dto.CreateInstrumentRequest;
import com.tradepilot.market.dto.InstrumentResponse;
import com.tradepilot.market.service.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrumentResponse createInstrument(
            @Valid @RequestBody CreateInstrumentRequest request) {

        return instrumentService.createInstrument(request);
    }

    @GetMapping("/{id}")
    public InstrumentResponse getInstrument(
            @PathVariable Long id) {

        return instrumentService.getInstrument(id);
    }

    @GetMapping
    public List<InstrumentResponse> getActiveInstruments() {

        return instrumentService.getActiveInstruments();
    }
}