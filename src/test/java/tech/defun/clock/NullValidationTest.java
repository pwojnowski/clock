package tech.defun.clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite to verify null validation for all public methods
 */
class NullValidationTest {

    @AfterEach
    void tearDown() {
        // Restore default clock after each test
        Clock.restoreDefaultClock();
    }

    @Test
    void testSetCurrentClockWithNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.setCurrentClock(null)
        );
        
        assertEquals("Clock implementation cannot be null", exception.getMessage());
    }

    @Test
    void testSetFixedClockWithNullDateTime() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.setFixedClock((LocalDateTime) null)
        );
        
        assertEquals("DateTime cannot be null", exception.getMessage());
    }

    @Test
    void testSetFixedClockWithNullInstant() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.setFixedClock((Instant) null)
        );
        
        assertEquals("Instant cannot be null", exception.getMessage());
    }

    @Test
    void testSetFixedClockWithNullLocalDate() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.setFixedClock((LocalDate) null)
        );
        
        assertEquals("LocalDate cannot be null", exception.getMessage());
    }

    @Test
    void testToUTCDateWithNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.toUTCDate(null)
        );
        
        assertEquals("Instant cannot be null", exception.getMessage());
    }

    @Test
    void testToUTCDateTimeWithNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.toUTCDateTime(null)
        );
        
        assertEquals("Instant cannot be null", exception.getMessage());
    }

    @Test
    void testPreviousWithNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.previous(null)
        );
        
        assertEquals("Day of week cannot be null", exception.getMessage());
    }

    @Test
    void testNextWithNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Clock.next(null)
        );
        
        assertEquals("Day of week cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockConstructorWithNullDate() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new FixedDateTimeClock((LocalDate) null)
        );
        
        assertEquals("Date cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockConstructorWithNullDateTime() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new FixedDateTimeClock((LocalDateTime) null)
        );
        
        assertEquals("DateTime cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockConstructorWithNullInstant() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new FixedDateTimeClock((Instant) null)
        );
        
        assertEquals("Instant cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockAdvanceByWithNullTemporalAmount() {
        FixedDateTimeClock clock = new FixedDateTimeClock(LocalDate.of(2023, 6, 15));
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> clock.advanceBy(null)
        );
        
        assertEquals("Temporal amount cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockAdvanceByWithNullTemporalUnit() {
        FixedDateTimeClock clock = new FixedDateTimeClock(LocalDate.of(2023, 6, 15));
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> clock.advanceBy(5, null)
        );
        
        assertEquals("Temporal unit cannot be null", exception.getMessage());
    }

    @Test
    void testFixedDateTimeClockReverseByWithNullTemporalUnit() {
        FixedDateTimeClock clock = new FixedDateTimeClock(LocalDate.of(2023, 6, 15));
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> clock.reverseBy(5, null)
        );
        
        assertEquals("Temporal unit cannot be null", exception.getMessage());
    }

    @Test
    void testClockContinuesToWorkAfterNullValidationFails() {
        // Verify that after a null validation failure, the clock still works
        try {
            Clock.setCurrentClock(null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        
        // Clock should still work with previous valid state
        LocalDate today = Clock.today();
        assertNotNull(today);
    }

    @Test
    void testValidInputsStillWorkAfterNullChecks() {
        // Ensure null checks don't break valid operations
        Clock.setFixedClock(LocalDateTime.of(2023, 6, 15, 14, 30));
        assertEquals(LocalDate.of(2023, 6, 15), Clock.today());
        
        Instant instant = Instant.parse("2023-06-15T14:30:45Z");
        assertEquals(LocalDate.of(2023, 6, 15), Clock.toUTCDate(instant));
        
        assertEquals(LocalDate.of(2023, 6, 14), Clock.previous(DayOfWeek.WEDNESDAY));
        assertEquals(LocalDate.of(2023, 6, 16), Clock.next(DayOfWeek.FRIDAY));
    }

    @Test
    void testFixedClockAdvanceByWithValidInputs() {
        FixedDateTimeClock clock = new FixedDateTimeClock(LocalDate.of(2023, 6, 15));
        
        clock.advanceBy(5, ChronoUnit.DAYS);
        assertEquals(LocalDate.of(2023, 6, 20), clock.today());
    }
}

