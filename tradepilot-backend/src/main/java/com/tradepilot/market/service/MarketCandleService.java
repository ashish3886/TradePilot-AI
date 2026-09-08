package com.tradepilot.market.service;

import com.tradepilot.exception.DuplicateResourceException;
import com.tradepilot.exception.ResourceNotFoundException;
import com.tradepilot.market.dto.CreateMarketCandleRequest;
import com.tradepilot.market.dto.MarketCandleResponse;
import com.tradepilot.market.entity.Instrument;
import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.repository.InstrumentRepository;
import com.tradepilot.market.repository.MarketCandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketCandleService {

    private final MarketCandleRepository candleRepository;
    private final InstrumentRepository instrumentRepository;

    @Transactional
    public MarketCandleResponse createCandle(
            CreateMarketCandleRequest request) {

        if (candleRepository
                .existsByInstrumentIdAndTimeframeAndCandleTimestamp(
                        request.instrumentId(),
                        request.timeframe(),
                        request.candleTimestamp())) {

            throw new DuplicateResourceException(
                    "Candle already exists for instrument, timeframe and timestamp"
            );
        }

        Instrument instrument = instrumentRepository
                .findById(request.instrumentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instrument not found: "
                                        + request.instrumentId()
                        ));

        validatePrices(request);

        MarketCandle candle = MarketCandle.builder()
                .instrument(instrument)
                .candleTimestamp(request.candleTimestamp())
                .timeframe(request.timeframe())
                .openPrice(request.openPrice())
                .highPrice(request.highPrice())
                .lowPrice(request.lowPrice())
                .closePrice(request.closePrice())
                .volume(request.volume())
                .build();

        return toResponse(candleRepository.save(candle));
    }

    @Transactional(readOnly = true)
    public List<MarketCandleResponse> getCandles(
            Long instrumentId,
            String timeframe,
            OffsetDateTime from,
            OffsetDateTime to) {

        var parsedTimeframe =
                com.tradepilot.market.entity.Timeframe
                        .valueOf(timeframe);

        return candleRepository
                .findByInstrumentIdAndTimeframeAndCandleTimestampBetweenOrderByCandleTimestampAsc(
                        instrumentId,
                        parsedTimeframe,
                        from,
                        to
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validatePrices(
            CreateMarketCandleRequest request) {

        if (request.highPrice()
                .compareTo(request.lowPrice()) < 0) {

            throw new IllegalArgumentException(
                    "High price cannot be lower than low price"
            );
        }

        if (request.openPrice()
                .compareTo(request.lowPrice()) < 0 ||
            request.openPrice()
                .compareTo(request.highPrice()) > 0) {

            throw new IllegalArgumentException(
                    "Open price must be between low and high"
            );
        }

        if (request.closePrice()
                .compareTo(request.lowPrice()) < 0 ||
            request.closePrice()
                .compareTo(request.highPrice()) > 0) {

            throw new IllegalArgumentException(
                    "Close price must be between low and high"
            );
        }
    }

    private MarketCandleResponse toResponse(
            MarketCandle candle) {

        return new MarketCandleResponse(
                candle.getId(),
                candle.getInstrument().getId(),
                candle.getInstrument().getSymbol(),
                candle.getCandleTimestamp(),
                candle.getTimeframe(),
                candle.getOpenPrice(),
                candle.getHighPrice(),
                candle.getLowPrice(),
                candle.getClosePrice(),
                candle.getVolume()
        );
    }
}