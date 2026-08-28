package com.tradepilot.market.service;


import com.tradepilot.exception.DuplicateResourceException;
import com.tradepilot.exception.ResourceNotFoundException;
import com.tradepilot.market.dto.CreateInstrumentRequest;
import com.tradepilot.market.dto.InstrumentResponse;
import com.tradepilot.market.entity.Instrument;
import com.tradepilot.market.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    @Transactional
    public InstrumentResponse createInstrument(
            CreateInstrumentRequest request) {

        if (instrumentRepository.existsBySymbol(request.symbol())) {
            throw new DuplicateResourceException(
                    "Instrument already exists: " + request.symbol()
            );
        }

        Instrument instrument = Instrument.builder()
                .symbol(request.symbol())
                .name(request.name())
                .exchange(request.exchange())
                .instrumentType(request.instrumentType())
                .active(true)
                .build();

        Instrument saved = instrumentRepository.save(instrument);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InstrumentResponse getInstrument(Long id) {

        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instrument not found: " + id
                        ));

        return toResponse(instrument);
    }

    @Transactional(readOnly = true)
    public List<InstrumentResponse> getActiveInstruments() {

        return instrumentRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InstrumentResponse toResponse(Instrument instrument) {

        return new InstrumentResponse(
                instrument.getId(),
                instrument.getSymbol(),
                instrument.getName(),
                instrument.getExchange(),
                instrument.getInstrumentType(),
                instrument.isActive(),
                instrument.getCreatedAt(),
                instrument.getUpdatedAt()
        );
    }
}