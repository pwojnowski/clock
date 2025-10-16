package tech.defun.clock;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.util.Objects.requireNonNull;

/**
 * A utility class providing convenient static methods for working with dates and times in UTC.
 * <p>
 * This class simplifies common date and time operations by providing a clean API that always
 * operates in UTC timezone. It supports both production use (with system time) and testing
 * scenarios (with fixed time).
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>All operations use UTC timezone for consistency</li>
 *   <li>Time values are truncated to microsecond precision</li>
 *   <li>Support for relative date calculations (yesterday, tomorrow, n days ago, etc.)</li>
 *   <li>Support for day-of-week navigation (previous/next Monday, Friday, etc.)</li>
 *   <li>Pluggable clock implementation for testing with {@link #setFixedClock(LocalDateTime)}</li>
 * </ul>
 * 
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Get current date and time
 * LocalDate today = Clock.today();
 * Instant now = Clock.now();
 * 
 * // Relative dates
 * LocalDate yesterday = Clock.yesterday();
 * LocalDate nextWeek = Clock.inDays(7);
 * LocalDate lastYear = Clock.yearsAgo(1);
 * 
 * // Day of week navigation
 * LocalDate previousMonday = Clock.previous(DayOfWeek.MONDAY);
 * LocalDate nextFriday = Clock.next(DayOfWeek.FRIDAY);
 * 
 * // Testing with fixed time
 * Clock.setFixedClock(LocalDateTime.of(2024, 1, 15, 10, 30));
 * LocalDate fixedToday = Clock.today(); // Always returns 2024-01-15
 * Clock.restoreDefaultClock(); // Resume using system time
 * }</pre>
 * 
 * <p>
 * This class is thread-safe for reading operations, but changing the clock implementation
 * via {@link #setCurrentClock(ClockSPI)} should be done carefully in multi-threaded environments,
 * typically only in test setup/teardown code.
 * </p>
 * 
 * @see ClockSPI
 * @see UTCClock
 * @see FixedDateTimeClock
 */
public final class Clock {

    private static ClockSPI CURRENT_CLOCK = new UTCClock();

    /**
     * Sets a custom clock implementation to be used by all Clock methods.
     * This allows for custom time sources, such as fixed clocks for testing.
     *
     * @param newClock the clock implementation to use
     * @throws NullPointerException if newClock is null
     */
    public static void setCurrentClock(ClockSPI newClock) {
        CURRENT_CLOCK = requireNonNull(newClock, "Clock implementation cannot be null");
    }

    /**
     * Sets the clock to a fixed time based on the current date at midnight UTC.
     * Useful for testing scenarios where a consistent time is needed.
     */
    public static void setFixedClock() {
        setFixedClock(LocalDate.now());
    }

    /**
     * Sets the clock to a fixed time based on the specified instant.
     * All subsequent calls to clock methods will return values based on this fixed instant.
     * The instant will be converted to UTC for date calculations.
     *
     * @param instant the fixed instant to use
     * @throws NullPointerException if instant is null
     */
    public static void setFixedClock(Instant instant) {
        requireNonNull(instant, "Instant cannot be null");
        setCurrentClock(new FixedDateTimeClock(instant));
    }

    /**
     * Sets the clock to a fixed date at midnight UTC.
     * All subsequent calls to clock methods will return values based on this fixed date.
     *
     * @param localDate the fixed date to use (time will be set to midnight UTC)
     * @throws NullPointerException if localDate is null
     */
    public static void setFixedClock(LocalDate localDate) {
        requireNonNull(localDate, "LocalDate cannot be null");
        setCurrentClock(new FixedDateTimeClock(localDate));
    }

    /**
     * Sets the clock to a fixed time based on the specified date and time in UTC.
     * All subsequent calls to clock methods will return values based on this fixed time.
     *
     * @param dateTime the fixed date and time to use (in UTC)
     * @throws NullPointerException if dateTime is null
     */
    public static void setFixedClock(LocalDateTime dateTime) {
        requireNonNull(dateTime, "DateTime cannot be null");
        setCurrentClock(new FixedDateTimeClock(dateTime));
    }

    /**
     * Restores the default clock implementation that returns the current system time in UTC.
     * This is typically used to reset the clock after using a fixed clock for testing.
     */
    public static void restoreDefaultClock() {
        setCurrentClock(new UTCClock());
    }

    /**
     * Returns the date for yesterday in UTC.
     *
     * @return yesterday's date
     */
    public static LocalDate yesterday() {
        return today().minusDays(1);
    }

    /**
     * Returns the current date in UTC.
     *
     * @return today's date
     */
    public static LocalDate today() {
        return CURRENT_CLOCK.today();
    }

    /**
     * Returns the date for tomorrow in UTC.
     *
     * @return tomorrow's date
     */
    public static LocalDate tomorrow() {
        return today().plusDays(1);
    }

    /**
     * Returns the date that was n days ago from today in UTC.
     *
     * @param n the number of days to subtract from today
     * @return the date n days ago
     */
    public static LocalDate daysAgo(int n) {
        return today().minusDays(n);
    }

    /**
     * Returns the date that will be in n days from today in UTC.
     *
     * @param n the number of days to add to today
     * @return the date in n days
     */
    public static LocalDate inDays(int n) {
        return today().plusDays(n);
    }

    /**
     * Returns the current instant in UTC, truncated to microsecond precision.
     *
     * @return the current instant
     */
    public static Instant now() {
        return CURRENT_CLOCK.now().truncatedTo(MICROS);
    }

    /**
     * Returns the current date and time in UTC, truncated to microsecond precision.
     *
     * @return the current date and time
     */
    public static LocalDateTime nowDateTime() {
        return CURRENT_CLOCK.nowDateTime().truncatedTo(MICROS);
    }

    /**
     * Converts an instant to a local date in the UTC time zone.
     *
     * @param instant the instant to convert
     * @return the local date in UTC
     * @throws NullPointerException if instant is null
     */
    public static LocalDate toUTCDate(Instant instant) {
        requireNonNull(instant, "Instant cannot be null");
        return instant.atZone(UTC).toLocalDate();
    }

    /**
     * Converts an instant to a local date-time in the UTC time zone.
     *
     * @param instant the instant to convert
     * @return the local date-time in UTC
     * @throws NullPointerException if instant is null
     */
    public static LocalDateTime toUTCDateTime(Instant instant) {
        requireNonNull(instant, "Instant cannot be null");
        return instant.atZone(UTC).toLocalDateTime();
    }

    /**
     * Returns the date of the previous occurrence of the specified day of the week.
     * For example, if today is Wednesday and you call previous(MONDAY), it returns the date of the previous Monday.
     *
     * @param dayOfWeek the day of week to find
     * @return the date of the previous occurrence of the specified day
     * @throws NullPointerException if dayOfWeek is null
     */
    public static LocalDate previous(DayOfWeek dayOfWeek) {
        requireNonNull(dayOfWeek, "Day of week cannot be null");
        return today().with(TemporalAdjusters.previous(dayOfWeek));
    }

    /**
     * Returns the date of the next occurrence of the specified day of the week.
     * For example, if today is Wednesday and you call next(FRIDAY), it returns the date of the next Friday.
     *
     * @param dayOfWeek the day of week to find
     * @return the date of the next occurrence of the specified day
     * @throws NullPointerException if dayOfWeek is null
     */
    public static LocalDate next(DayOfWeek dayOfWeek) {
        requireNonNull(dayOfWeek, "Day of week cannot be null");
        return today().with(TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * Returns the date that was n years ago from today in UTC.
     *
     * @param n the number of years to subtract from today
     * @return the date n years ago
     */
    public static LocalDate yearsAgo(int n) {
        return today().minusYears(n);
    }

    /**
     * Returns the date that will be in n years from today in UTC.
     *
     * @param n the number of years to add to today
     * @return the date in n years
     */
    public static LocalDate inYears(int n) {
        return today().plusYears(n);
    }

    /**
     * Returns the date that was n months ago from today in UTC.
     *
     * @param n the number of months to subtract from today
     * @return the date n months ago
     */
    public static LocalDate monthsAgo(int n) {
        return today().minusMonths(n);
    }

    /**
     * Returns the date that will be in n months from today in UTC.
     *
     * @param n the number of months to add to today
     * @return the date in n months
     */
    public static LocalDate inMonths(int n) {
        return today().plusMonths(n);
    }

}