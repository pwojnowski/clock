package tech.defun.clock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;

class FixedDateTimeClockTest {

    @Test
    void testConstructorWithLocalDate() {
        LocalDate date = LocalDate.of(2023, 6, 15);
        FixedDateTimeClock clock = new FixedDateTimeClock(date);
        
        assertEquals(date, clock.today());
        assertEquals(date.atStartOfDay(), clock.nowDateTime());
        assertEquals(date.atStartOfDay().toInstant(UTC), clock.now());
    }

    @Test
    void testConstructorWithLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        assertEquals(LocalDate.of(2023, 6, 15), clock.today());
        assertEquals(dateTime, clock.nowDateTime());
        assertEquals(dateTime.toInstant(UTC), clock.now());
    }

    @Test
    void testConstructorWithInstant() {
        Instant instant = Instant.parse("2023-06-15T14:30:45.123456Z");
        FixedDateTimeClock clock = new FixedDateTimeClock(instant);
        
        assertEquals(LocalDate.of(2023, 6, 15), clock.today());
        assertEquals(LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456000), clock.nowDateTime());
        assertEquals(instant, clock.now());
    }

    @Test
    void testAdvanceByTemporalAmount() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(Duration.ofHours(2));
        
        assertEquals(LocalDateTime.of(2023, 6, 15, 16, 30, 45), clock.nowDateTime());
    }

    @Test
    void testAdvanceByTemporalAmountDays() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(Duration.ofDays(3));
        
        assertEquals(LocalDate.of(2023, 6, 18), clock.today());
        assertEquals(LocalDateTime.of(2023, 6, 18, 14, 30, 45), clock.nowDateTime());
    }

    @Test
    void testAdvanceByAmountAndUnit() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(5, ChronoUnit.DAYS);
        
        assertEquals(LocalDate.of(2023, 6, 20), clock.today());
        assertEquals(LocalDateTime.of(2023, 6, 20, 14, 30, 45), clock.nowDateTime());
    }

    @Test
    void testAdvanceByHours() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(3, ChronoUnit.HOURS);
        
        assertEquals(LocalDateTime.of(2023, 6, 15, 17, 30, 45), clock.nowDateTime());
    }

    @Test
    void testAdvanceByMinutes() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(45, ChronoUnit.MINUTES);
        
        assertEquals(LocalDateTime.of(2023, 6, 15, 15, 15, 45), clock.nowDateTime());
    }

    @Test
    void testReverseBy() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.reverseBy(3, ChronoUnit.DAYS);
        
        assertEquals(LocalDate.of(2023, 6, 12), clock.today());
        assertEquals(LocalDateTime.of(2023, 6, 12, 14, 30, 45), clock.nowDateTime());
    }

    @Test
    void testReverseByHours() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.reverseBy(5, ChronoUnit.HOURS);
        
        assertEquals(LocalDateTime.of(2023, 6, 15, 9, 30, 45), clock.nowDateTime());
    }

    @Test
    void testChainedAdvances() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(1, ChronoUnit.DAYS)
             .advanceBy(2, ChronoUnit.HOURS)
             .advanceBy(30, ChronoUnit.MINUTES);
        
        assertEquals(LocalDateTime.of(2023, 6, 16, 17, 0, 45), clock.nowDateTime());
    }

    @Test
    void testChainedAdvancesAndReverses() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(5, ChronoUnit.DAYS)
             .reverseBy(2, ChronoUnit.DAYS)
             .advanceBy(1, ChronoUnit.HOURS);
        
        assertEquals(LocalDate.of(2023, 6, 18), clock.today());
        assertEquals(LocalDateTime.of(2023, 6, 18, 15, 30, 45), clock.nowDateTime());
    }

    @Test
    void testAdvanceAcrossMonthBoundary() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 28, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(5, ChronoUnit.DAYS);
        
        assertEquals(LocalDate.of(2023, 7, 3), clock.today());
    }

    @Test
    void testAdvanceAcrossYearBoundary() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 30, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(5, ChronoUnit.DAYS);
        
        assertEquals(LocalDate.of(2024, 1, 4), clock.today());
    }

    @Test
    void testReverseAcrossMonthBoundary() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 7, 3, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.reverseBy(5, ChronoUnit.DAYS);
        
        assertEquals(LocalDate.of(2023, 6, 28), clock.today());
    }

    @Test
    void testTodayReturnsCorrectDate() {
        LocalDate date = LocalDate.of(2023, 3, 17);
        FixedDateTimeClock clock = new FixedDateTimeClock(date);
        
        assertEquals(date, clock.today());
    }

    @Test
    void testNowReturnsCorrectInstant() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456789);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        Instant expected = dateTime.toInstant(UTC);
        assertEquals(expected, clock.now());
    }

    @Test
    void testNowDateTimeReturnsCorrectDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456789);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        assertEquals(dateTime, clock.nowDateTime());
    }

    @Test
    void testAdvanceByZero() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(0, ChronoUnit.DAYS);
        
        assertEquals(dateTime, clock.nowDateTime());
    }

    @Test
    void testReverseByZero() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.reverseBy(0, ChronoUnit.DAYS);
        
        assertEquals(dateTime, clock.nowDateTime());
    }

    @Test
    void testAdvanceByWeeks() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(2, ChronoUnit.WEEKS);
        
        assertEquals(LocalDate.of(2023, 6, 29), clock.today());
    }

    @Test
    void testAdvanceByMonths() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(3, ChronoUnit.MONTHS);
        
        assertEquals(LocalDate.of(2023, 9, 15), clock.today());
    }

    @Test
    void testAdvanceByYears() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        FixedDateTimeClock clock = new FixedDateTimeClock(dateTime);
        
        clock.advanceBy(2, ChronoUnit.YEARS);
        
        assertEquals(LocalDate.of(2025, 6, 15), clock.today());
    }
}

