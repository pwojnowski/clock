package tech.defun.clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ClockTest {

    @BeforeEach
    void setUp() {
        // Ensure we start with a clean state
        Clock.restoreDefaultClock();
    }

    @AfterEach
    void tearDown() {
        // Restore default clock after each test
        Clock.restoreDefaultClock();
    }

    @Test
    void testSetFixedClockWithNoArgs() {
        LocalDate beforeSet = LocalDate.now();
        Clock.setFixedClock();
        LocalDate today = Clock.today();

        // Should be fixed to current date
        assertTrue(today.equals(beforeSet) || today.equals(beforeSet.plusDays(1)));
    }

    @Test
    void testSetFixedClockWithLocalDateTime() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        Clock.setFixedClock(fixedDateTime);

        assertEquals(LocalDate.of(2023, 6, 15), Clock.today());
        assertEquals(fixedDateTime.truncatedTo(ChronoUnit.MICROS), Clock.nowDateTime());
    }

    @Test
    void testRestoreDefaultClock() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        Clock.setFixedClock(fixedDateTime);
        assertEquals(LocalDate.of(2023, 6, 15), Clock.today());

        Clock.restoreDefaultClock();
        LocalDate today = Clock.today();
        LocalDate actualToday = LocalDate.now();

        // Should be back to system time (might be off by 1 day due to timing)
        assertTrue(today.equals(actualToday)
                || today.equals(actualToday.plusDays(1))
                || today.equals(actualToday.minusDays(1))
        );
    }

    @Test
    void testToday() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(fixedDate, Clock.today());
    }

    @Test
    void testYesterday() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 6, 14), Clock.yesterday());
    }

    @Test
    void testTomorrow() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 6, 16), Clock.tomorrow());
    }

    @Test
    void testDaysAgo() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 6, 10), Clock.daysAgo(5));
        assertEquals(LocalDate.of(2023, 6, 14), Clock.daysAgo(1));
        assertEquals(LocalDate.of(2023, 6, 15), Clock.daysAgo(0));
    }

    @Test
    void testInDays() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 6, 20), Clock.inDays(5));
        assertEquals(LocalDate.of(2023, 6, 16), Clock.inDays(1));
        assertEquals(LocalDate.of(2023, 6, 15), Clock.inDays(0));
    }

    @Test
    void testYearsAgo() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2020, 6, 15), Clock.yearsAgo(3));
        assertEquals(LocalDate.of(2022, 6, 15), Clock.yearsAgo(1));
    }

    @Test
    void testInYears() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2026, 6, 15), Clock.inYears(3));
        assertEquals(LocalDate.of(2024, 6, 15), Clock.inYears(1));
    }

    @Test
    void testMonthsAgo() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 3, 15), Clock.monthsAgo(3));
        assertEquals(LocalDate.of(2023, 5, 15), Clock.monthsAgo(1));
    }

    @Test
    void testInMonths() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        assertEquals(LocalDate.of(2023, 9, 15), Clock.inMonths(3));
        assertEquals(LocalDate.of(2023, 7, 15), Clock.inMonths(1));
    }

    @Test
    void testNow() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456789);
        Clock.setFixedClock(fixedDateTime);

        Instant now = Clock.now();
        Instant expected = fixedDateTime.toInstant(java.time.ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);

        assertEquals(expected, now);
    }

    @Test
    void testNowDateTime() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456789);
        Clock.setFixedClock(fixedDateTime);

        LocalDateTime now = Clock.nowDateTime();
        LocalDateTime expected = fixedDateTime.truncatedTo(ChronoUnit.MICROS);

        assertEquals(expected, now);
    }

    @Test
    void testToUTCDate() {
        Instant instant = Instant.parse("2023-06-15T14:30:45.123456Z");
        LocalDate date = Clock.toUTCDate(instant);

        assertEquals(LocalDate.of(2023, 6, 15), date);
    }

    @Test
    void testToUTCDateTime() {
        Instant instant = Instant.parse("2023-06-15T14:30:45.123456Z");
        LocalDateTime dateTime = Clock.toUTCDateTime(instant);

        assertEquals(LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456000), dateTime);
    }

    @Test
    void testPreviousDayOfWeek() {
        // June 15, 2023 is a Thursday
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        // Previous Wednesday should be June 14
        assertEquals(LocalDate.of(2023, 6, 14), Clock.previous(DayOfWeek.WEDNESDAY));

        // Previous Monday should be June 12
        assertEquals(LocalDate.of(2023, 6, 12), Clock.previous(DayOfWeek.MONDAY));

        // Previous Sunday should be June 11
        assertEquals(LocalDate.of(2023, 6, 11), Clock.previous(DayOfWeek.SUNDAY));
    }

    @Test
    void testNextDayOfWeek() {
        // June 15, 2023 is a Thursday
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate.atStartOfDay());

        // Next Friday should be June 16
        assertEquals(LocalDate.of(2023, 6, 16), Clock.next(DayOfWeek.FRIDAY));

        // Next Monday should be June 19
        assertEquals(LocalDate.of(2023, 6, 19), Clock.next(DayOfWeek.MONDAY));

        // Next Sunday should be June 18
        assertEquals(LocalDate.of(2023, 6, 18), Clock.next(DayOfWeek.SUNDAY));
    }

    @Test
    void testSetCurrentClockWithCustomImplementation() {
        FixedDateTimeClock customClock = new FixedDateTimeClock(LocalDate.of(2025, 12, 25));
        Clock.setCurrentClock(customClock);

        assertEquals(LocalDate.of(2025, 12, 25), Clock.today());
    }

    @Test
    void testMicrosecondPrecision() {
        // Test that time is truncated to microseconds (not nanoseconds)
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456789);
        Clock.setFixedClock(dateTime);

        LocalDateTime now = Clock.nowDateTime();

        // Should be truncated to microseconds (123456000 nanoseconds)
        assertEquals(123456000, now.getNano());
    }

    @Test
    void testSetFixedClockWithInstant() {
        Instant fixedInstant = Instant.parse("2023-06-15T14:30:45.123456Z");
        Clock.setFixedClock(fixedInstant);

        assertEquals(LocalDate.of(2023, 6, 15), Clock.today());
        assertEquals(fixedInstant.truncatedTo(ChronoUnit.MICROS), Clock.now());
        assertEquals(LocalDateTime.of(2023, 6, 15, 14, 30, 45, 123456000), Clock.nowDateTime());
    }

    @Test
    void testSetFixedClockWithLocalDate() {
        LocalDate fixedDate = LocalDate.of(2023, 6, 15);
        Clock.setFixedClock(fixedDate);

        assertEquals(fixedDate, Clock.today());
        // Time should be midnight UTC
        assertEquals(LocalDateTime.of(2023, 6, 15, 0, 0, 0), Clock.nowDateTime());
    }
}

