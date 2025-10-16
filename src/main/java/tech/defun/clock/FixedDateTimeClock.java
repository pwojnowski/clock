package tech.defun.clock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

class FixedDateTimeClock implements ClockSPI {

    private LocalDateTime datetime;

    public FixedDateTimeClock(LocalDate date) {
        requireNonNull(date, "Date cannot be null");
        this.datetime = date.atStartOfDay();
    }

    public FixedDateTimeClock(LocalDateTime dateTime) {
        this.datetime = requireNonNull(dateTime, "DateTime cannot be null");
    }

    public FixedDateTimeClock(Instant instant) {
        requireNonNull(instant, "Instant cannot be null");
        this.datetime = instant.atOffset(UTC).toLocalDateTime();
    }

    @Override
    public ClockSPI advanceBy(TemporalAmount temporalAmount) {
        requireNonNull(temporalAmount, "Temporal amount cannot be null");
        datetime = datetime.plus(temporalAmount);
        return this;
    }

    @Override
    public ClockSPI advanceBy(long amount, TemporalUnit unit) {
        requireNonNull(unit, "Temporal unit cannot be null");
        datetime = datetime.plus(amount, unit);
        return this;
    }

    @Override
    public ClockSPI reverseBy(long amount, TemporalUnit unit) {
        requireNonNull(unit, "Temporal unit cannot be null");
        datetime = datetime.minus(amount, unit);
        return this;
    }

    @Override
    public LocalDate today() {
        return datetime.toLocalDate();
    }

    @Override
    public Instant now() {
        return datetime.toInstant(UTC);
    }

    @Override
    public LocalDateTime nowDateTime() {
        return datetime;
    }
}
