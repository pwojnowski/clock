package tech.defun.clock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

class UTCClock implements ClockSPI {

    private static final String EXCEPTION_MESSAGE = "Tried to modify " + UTCClock.class.getSimpleName();

    @Override
    public ClockSPI advanceBy(TemporalAmount temporalAmount) {
        // Can't change the real clock!
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public ClockSPI advanceBy(long amount, TemporalUnit unit) {
        // Can't change the real clock!
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public ClockSPI reverseBy(long amount, TemporalUnit unit) {
        // Can't change the real clock!
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public Instant now() {
        return Instant.now();
    }

    @Override
    public LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }
}
