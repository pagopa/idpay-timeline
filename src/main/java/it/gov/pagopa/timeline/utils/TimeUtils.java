package it.gov.pagopa.timeline.utils;


import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class TimeUtils {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Rome");

    private TimeUtils() {
    }

    public static Instant startOfDay(Instant instant) {
        return instant.atZone(ZONE_ID)
                .toLocalDate()
                .atStartOfDay(ZONE_ID)
                .toInstant();
    }

    public static Instant endOfDay(Instant instant) {
        return instant.atZone(ZONE_ID)
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(ZONE_ID)
                .toInstant();
    }
}
