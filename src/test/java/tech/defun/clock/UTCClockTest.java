package tech.defun.clock;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class UTCClockTest {

    @Test
    void testTodayReturnsCurrentDate() {
        UTCClock clock = new UTCClock();
        LocalDate today = clock.today();
        LocalDate actualToday = LocalDate.now();
        
        // The dates should match (might be off by 1 if test runs at midnight boundary)
        assertTrue(today.equals(actualToday)
                || today.equals(actualToday.plusDays(1))
                || today.equals(actualToday.minusDays(1)));
    }

    @Test
    void testNowReturnsCurrentInstant() {
        UTCClock clock = new UTCClock();
        Instant before = Instant.now();
        Instant now = clock.now();
        Instant after = Instant.now();
        
        // The instant should be between before and after (within a reasonable window)
        assertFalse(now.isBefore(before.minusSeconds(1)));
        assertFalse(now.isAfter(after.plusSeconds(1)));
    }

    @Test
    void testNowDateTimeReturnsCurrentDateTime() {
        UTCClock clock = new UTCClock();
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime now = clock.nowDateTime();
        LocalDateTime after = LocalDateTime.now();
        
        // The datetime should be between before and after (within a reasonable window)
        assertFalse(now.isBefore(before.minusSeconds(1)));
        assertFalse(now.isAfter(after.plusSeconds(1)));
    }

    @Test
    void testAdvanceByTemporalAmountThrowsException() {
        UTCClock clock = new UTCClock();
        
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.advanceBy(Duration.ofHours(1))
        );
        
        assertTrue(exception.getMessage().contains("UTCClock"));
    }

    @Test
    void testAdvanceByAmountAndUnitThrowsException() {
        UTCClock clock = new UTCClock();
        
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.advanceBy(5, ChronoUnit.DAYS)
        );
        
        assertTrue(exception.getMessage().contains("UTCClock"));
    }

    @Test
    void testReverseByThrowsException() {
        UTCClock clock = new UTCClock();
        
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.reverseBy(3, ChronoUnit.DAYS)
        );
        
        assertTrue(exception.getMessage().contains("UTCClock"));
    }

    @Test
    void testMultipleCallsToTodayReturnSameOrNextDate() {
        UTCClock clock = new UTCClock();
        LocalDate first = clock.today();
        LocalDate second = clock.today();
        
        // Should be same date or next date (if day changed between calls)
        assertTrue(first.equals(second) || first.plusDays(1).equals(second));
    }

    @Test
    void testMultipleCallsToNowReturnIncreasingOrEqualInstants() {
        UTCClock clock = new UTCClock();
        Instant first = clock.now();
        Instant second = clock.now();
        
        // Second call should be at same time or later
        assertFalse(second.isBefore(first));
    }

    @Test
    void testExceptionMessageContainsClassName() {
        UTCClock clock = new UTCClock();
        
        try {
            clock.advanceBy(1, ChronoUnit.DAYS);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("UTCClock"), 
                "Exception message should contain 'UTCClock'");
        }
    }

    @Test
    void testAllAdvancementMethodsThrowSameException() {
        UTCClock clock = new UTCClock();
        
        UnsupportedOperationException exception1 = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.advanceBy(Duration.ofHours(1))
        );
        
        UnsupportedOperationException exception2 = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.advanceBy(1, ChronoUnit.HOURS)
        );
        
        UnsupportedOperationException exception3 = assertThrows(
            UnsupportedOperationException.class,
            () -> clock.reverseBy(1, ChronoUnit.HOURS)
        );
        
        // All three should throw the same exception message
        assertEquals(exception1.getMessage(), exception2.getMessage());
        assertEquals(exception2.getMessage(), exception3.getMessage());
    }

    @Test
    void testNowIsSensible() {
        UTCClock clock = new UTCClock();
        Instant now = clock.now();
        
        // Check that the time is after 2020 and before 2100
        Instant after2020 = Instant.parse("2020-01-01T00:00:00Z");
        Instant before2100 = Instant.parse("2100-01-01T00:00:00Z");
        
        assertTrue(now.isAfter(after2020), "Time should be after 2020");
        assertTrue(now.isBefore(before2100), "Time should be before 2100");
    }

    @Test
    void testTodayIsSensible() {
        UTCClock clock = new UTCClock();
        LocalDate today = clock.today();
        
        // Check that the date is after 2020 and before 2100
        LocalDate after2020 = LocalDate.of(2020, 1, 1);
        LocalDate before2100 = LocalDate.of(2100, 1, 1);
        
        assertTrue(today.isAfter(after2020), "Date should be after 2020");
        assertTrue(today.isBefore(before2100), "Date should be before 2100");
    }
}

