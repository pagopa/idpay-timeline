package it.gov.pagopa.timeline.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class TimeUtilsTest {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Rome");

    @Test
    void startOfDay_shouldReturnStartOfDay() {
        LocalDateTime inputDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        Instant inputInstant = inputDateTime.atZone(ZONE_ID).toInstant();

        Instant result = TimeUtils.startOfDay(inputInstant);

        Instant expected = LocalDate.of(2024, 1, 15)
                .atStartOfDay(ZONE_ID)
                .toInstant();

        assertEquals(expected, result);
    }

    @Test
    void endOfDay_shouldReturnEndOfDay() {
        LocalDateTime inputDateTime = LocalDateTime.of(2024, 1, 15, 10, 5, 12);
        Instant inputInstant = inputDateTime.atZone(ZONE_ID).toInstant();

        Instant result = TimeUtils.endOfDay(inputInstant);

        Instant expected = LocalDate.of(2024, 1, 15)
                .atTime(LocalTime.MAX)
                .atZone(ZONE_ID)
                .toInstant();

        assertEquals(expected, result);
    }

    @Test
    void startOfDay_whenAlreadyStartOfDay() {
        Instant inputInstant = LocalDate.of(2024, 3, 10)
                .atStartOfDay(ZONE_ID)
                .toInstant();

        Instant result = TimeUtils.startOfDay(inputInstant);

        assertEquals(inputInstant, result);
    }

    @Test
    void endOfDay_whenAlreadyEndOfDay() {
        Instant inputInstant = LocalDate.of(2024, 3, 10)
                .atTime(LocalTime.MAX)
                .atZone(ZONE_ID)
                .toInstant();

        Instant result = TimeUtils.endOfDay(inputInstant);

        assertEquals(inputInstant, result);
    }

    @Test
    void startOfDay_nullInstant() {
        assertThrows(NullPointerException.class, () -> TimeUtils.startOfDay(null));
    }

    @Test
    void endOfDay_nullInstant() {
        assertThrows(NullPointerException.class, () -> TimeUtils.endOfDay(null));
    }
}