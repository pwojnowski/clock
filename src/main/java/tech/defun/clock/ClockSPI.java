package tech.defun.clock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public interface ClockSPI {

    ClockSPI advanceBy(TemporalAmount temporalAmount);
    ClockSPI advanceBy(long amount, TemporalUnit unit);
    ClockSPI reverseBy(long amount, TemporalUnit unit);

    LocalDate today();
    Instant now();
    LocalDateTime nowDateTime();
}
