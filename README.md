# Clock Abstraction Library

A lightweight Java library providing a global clock abstraction to facilitate testing of time-related functionalities. The library uses a static API that allows you to control time globally in your tests.

## Features

- **Static API**: Simple, globally accessible time functions
- **UTC-based**: All timestamps are in UTC (microsecond precision)
- **Test-friendly**: Easy to set fixed time for testing
- **Rich Date API**: Convenient methods for date calculations
- **Zero Dependencies**: Uses only Java standard library (`java.time`)
- **Well-tested**: Comprehensive test suite with JUnit 5

## Requirements

- Java 17 or higher
- Maven 3.6+ (for building)

## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>tech.defun</groupId>
    <artifactId>clock</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Basic Usage

```java
import tech.defun.clock.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Production code - uses system clock by default
LocalDate today = Clock.today();
Instant now = Clock.now();
LocalDateTime dateTime = Clock.nowDateTime();
```

### Date Calculations

The library provides convenient methods for common date calculations:

```java
// Day calculations
LocalDate today = Clock.today();
LocalDate yesterday = Clock.yesterday();
LocalDate tomorrow = Clock.tomorrow();
LocalDate fiveDaysAgo = Clock.daysAgo(5);
LocalDate inTenDays = Clock.inDays(10);

// Month calculations
LocalDate threeMonthsAgo = Clock.monthsAgo(3);
LocalDate inSixMonths = Clock.inMonths(6);

// Year calculations
LocalDate lastYear = Clock.yearsAgo(1);
LocalDate nextYear = Clock.inYears(1);
```

### Day of Week Navigation

```java
import java.time.DayOfWeek;

// Find previous or next day of week
LocalDate previousMonday = Clock.previous(DayOfWeek.MONDAY);
LocalDate nextFriday = Clock.next(DayOfWeek.FRIDAY);
```

### Time Conversion

```java
import java.time.Instant;

// Convert Instant to UTC date/datetime
Instant instant = Instant.parse("2023-10-15T14:30:45Z");
LocalDate date = Clock.toUTCDate(instant);
LocalDateTime dateTime = Clock.toUTCDateTime(instant);
```

## Testing with Fixed Time

The primary feature for testing is the ability to set a fixed time globally:

### Setting Fixed Time

```java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.defun.clock.Clock;
import java.time.LocalDateTime;

class MyServiceTest {
    
    @AfterEach
    void tearDown() {
        // Always restore the default clock after tests
        Clock.restoreDefaultClock();
    }
    
    @Test
    void testWithFixedTime() {
        // Set clock to specific date and time
        LocalDateTime fixedTime = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        Clock.setFixedClock(fixedTime);
        
        // Now all Clock methods return values based on this fixed time
        assertEquals(LocalDate.of(2023, 6, 15), Clock.today());
        assertEquals(LocalDate.of(2023, 6, 14), Clock.yesterday());
        assertEquals(LocalDate.of(2023, 6, 16), Clock.tomorrow());
    }
    
    @Test
    void testWithCurrentTime() {
        // Set clock to current system date (but fixed)
        Clock.setFixedClock();
        
        LocalDate today = Clock.today();
        // Time is now frozen at the moment setFixedClock() was called
        
        Thread.sleep(1000);
        assertEquals(today, Clock.today()); // Still the same!
    }
}
```

### Restoring System Clock

```java
// Restore to using system clock (production behavior)
Clock.restoreDefaultClock();

// Now Clock.now() returns actual current time
Instant actualNow = Clock.now();
```

## Complete Testing Example

```java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.defun.clock.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

class SubscriptionServiceTest {
    
    @AfterEach
    void tearDown() {
        Clock.restoreDefaultClock();
    }
    
    @Test
    void testSubscriptionExpiry() {
        // Start at a known date
        Clock.setFixedClock(LocalDateTime.of(2023, 1, 1, 0, 0));
        
        SubscriptionService service = new SubscriptionService();
        Subscription sub = service.createSubscription(30); // 30-day subscription
        
        // Check expiry date is calculated correctly
        assertEquals(LocalDate.of(2023, 1, 31), sub.getExpiryDate());
        
        // Check active status at different dates
        assertTrue(sub.isActive());
        
        // Simulate time passing (change the global clock)
        Clock.setFixedClock(LocalDateTime.of(2023, 1, 31, 0, 0));
        assertFalse(sub.isActive()); // Expired
    }
    
    @Test
    void testBillingCycle() {
        Clock.setFixedClock(LocalDateTime.of(2023, 6, 15, 12, 0));
        
        BillingService billing = new BillingService();
        
        // Test with fixed time
        LocalDate nextBillingDate = billing.getNextBillingDate();
        assertEquals(LocalDate.of(2023, 7, 15), nextBillingDate);
        
        // Verify calculations relative to "today"
        LocalDate lastBillingDate = billing.getLastBillingDate();
        assertEquals(LocalDate.of(2023, 5, 15), lastBillingDate);
    }
}
```

## Production Code Example

```java
public class SubscriptionService {
    
    public Subscription createSubscription(int days) {
        // Uses Clock instead of LocalDate.now()
        LocalDate startDate = Clock.today();
        LocalDate expiryDate = Clock.inDays(days);
        
        return new Subscription(startDate, expiryDate);
    }
    
    public boolean isExpired(Subscription subscription) {
        // Uses Clock.today() - testable!
        return Clock.today().isAfter(subscription.getExpiryDate());
    }
    
    public Instant getCreationTimestamp() {
        // Uses Clock.now() for precise timestamps
        return Clock.now();
    }
}
```

## API Reference

### Current Time Methods

```java
// Get current date (UTC)
LocalDate today = Clock.today();

// Get current instant (truncated to microseconds)
Instant now = Clock.now();

// Get current datetime (truncated to microseconds)
LocalDateTime dateTime = Clock.nowDateTime();
```

### Date Calculations

```java
// Days
LocalDate yesterday = Clock.yesterday();
LocalDate tomorrow = Clock.tomorrow();
LocalDate daysAgo = Clock.daysAgo(int n);
LocalDate inDays = Clock.inDays(int n);

// Months
LocalDate monthsAgo = Clock.monthsAgo(int n);
LocalDate inMonths = Clock.inMonths(int n);

// Years
LocalDate yearsAgo = Clock.yearsAgo(int n);
LocalDate inYears = Clock.inYears(int n);
```

### Day of Week Navigation

```java
import java.time.DayOfWeek;

// Get previous/next occurrence of a day of week
LocalDate previous = Clock.previous(DayOfWeek dayOfWeek);
LocalDate next = Clock.next(DayOfWeek dayOfWeek);
```

### Time Conversion

```java
// Convert Instant to UTC date/datetime
LocalDate date = Clock.toUTCDate(Instant instant);
LocalDateTime dateTime = Clock.toUTCDateTime(Instant instant);
```

### Testing Control Methods

```java
// Set fixed clock for testing
Clock.setFixedClock();                              // Fixed to current system time
Clock.setFixedClock(LocalDateTime dateTime);        // Fixed to specific datetime

// Restore system clock (production behavior)
Clock.restoreDefaultClock();

// Advanced: Set custom clock implementation
Clock.setCurrentClock(ClockSPI implementation);
```

## Design Principles

### Global State

The library uses global state to manage the current clock implementation. This design:
- Makes it easy to use (no dependency injection required)
- Simplifies testing (just call `setFixedClock()`)
- **Not thread-safe**: Tests should not run in parallel when using fixed clocks
- Best suited for single-threaded test scenarios

### UTC Only

All time values are in UTC:
- `now()` returns `Instant` (always UTC)
- `today()` returns `LocalDate` (interpreted as UTC)
- `nowDateTime()` returns `LocalDateTime` (interpreted as UTC)
- Eliminates timezone-related issues in tests

### Microsecond Precision

Time values are truncated to microseconds (not nanoseconds) for consistency across different systems and databases.

## Best Practices

1. **Always restore the clock after tests**
   ```java
   @AfterEach
   void tearDown() {
       Clock.restoreDefaultClock();
   }
   ```

2. **Don't run tests in parallel** when using `setFixedClock()` (global state)

3. **Use Clock everywhere** - Replace `LocalDate.now()`, `Instant.now()`, etc. with `Clock.*` methods

4. **Keep production code simple** - No need for dependency injection, just use `Clock` directly

5. **Test in isolation** - Each test should set its own fixed time and clean up

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/clock.git
cd clock

# Build with Maven
mvn clean install

# Run tests
mvn test
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClockTest
```

Test coverage includes:
- 20 tests in `ClockTest` - Core functionality
- 23 tests in `FixedDateTimeClockTest` - Fixed clock behavior
- 12 tests in `UTCClockTest` - System clock behavior  
- 15 tests in `NullValidationTest` - Input validation

**Total: 70 tests, all passing**

## Thread Safety

⚠️ **Important**: This library is **not thread-safe**. 

The global clock state (`Clock.setFixedClock()`) should only be modified in single-threaded test scenarios. Do not:
- Run tests in parallel when using fixed clocks
- Call `setFixedClock()` from multiple threads
- Use this in multi-threaded production code that needs different time contexts

For thread-isolated time contexts, consider using dependency injection with separate clock instances.

## Limitations

1. **Global State**: Clock changes affect the entire JVM process
2. **Not Thread-Safe**: Cannot safely use different times in parallel tests
3. **Test-Oriented**: Designed primarily for testing, not for complex production time management
4. **UTC Only**: No timezone support (by design)

## Advanced Usage

### Custom Clock Implementation

You can provide your own clock implementation using the `ClockSPI` interface:

```java
import tech.defun.clock.ClockSPI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Create custom implementation (package-private interface)
// Note: You'll need to be in the tech.defun.clock package or
// use the provided implementations

// Use the provided FixedDateTimeClock directly
FixedDateTimeClock customClock = new FixedDateTimeClock(
    LocalDate.of(2025, 12, 25)
);
Clock.setCurrentClock(customClock);

// Now the clock is set to Christmas 2025
assertEquals(LocalDate.of(2025, 12, 25), Clock.today());
```

### Time Manipulation in Tests

The `FixedDateTimeClock` supports time manipulation (though not exposed through the main `Clock` API):

```java
import tech.defun.clock.FixedDateTimeClock;
import java.time.temporal.ChronoUnit;

FixedDateTimeClock clock = new FixedDateTimeClock(
    LocalDateTime.of(2023, 6, 15, 14, 30)
);
Clock.setCurrentClock(clock);

// Manipulate time
clock.advanceBy(5, ChronoUnit.DAYS);
assertEquals(LocalDate.of(2023, 6, 20), Clock.today());

clock.reverseBy(3, ChronoUnit.DAYS);
assertEquals(LocalDate.of(2023, 6, 17), Clock.today());

// Method chaining supported
clock.advanceBy(1, ChronoUnit.MONTHS)
     .advanceBy(2, ChronoUnit.HOURS)
     .reverseBy(30, ChronoUnit.MINUTES);
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or contributions, please visit the [GitHub repository](https://github.com/yourusername/clock).

## Changelog

### Version 1.0.0 (2025-10-16)
- Initial release
- Static Clock API with global state management
- `UTCClock` - System clock implementation
- `FixedDateTimeClock` - Fixed time for testing with manipulation support
- Comprehensive date calculation methods
- Microsecond precision timestamps
- Full input validation
- 70 tests with complete coverage
