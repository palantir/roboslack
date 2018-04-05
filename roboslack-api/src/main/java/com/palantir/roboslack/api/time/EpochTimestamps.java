/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.roboslack.api.time;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

/**
 * Converts a given {@link Temporal} into the epoch timestamp (second precision) format that Slack expects when handling
 * dates and times.  Currently not all {@link Temporal} types are handled, but the ones that are handled include: <ul>
 * <li>{@link Instant}</li> <li>{@link LocalTime}</li> <li>{@link LocalDate}</li> <li>{@link LocalDateTime}</li>
 * <li>{@link ZonedDateTime}</li> <li>{@link OffsetDateTime}</li> <li>{@link OffsetTime}</li> </ul>
 *
 * @see Temporal
 * @since 1.0.0
 */
final class EpochTimestamps {

    private static final ZoneOffset UTC_OFFSET = ZoneOffset.UTC;
    private static final ZoneId UTC_ID = ZoneId.of("UTC");

    private static final String CONVERT_ERR = "Unable to convert object of type '%s' to epoch timestamp";

    private EpochTimestamps() {}

    @SuppressWarnings("unused") // Called via reflection
    private static long convertInstant(Instant instant) {
        return instant.getEpochSecond();
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertLocalTime(LocalTime localTime) {
        return convertLocalDateTime(localTime.atDate(LocalDate.now(UTC_ID)));
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertLocalDate(LocalDate localDate) {
        return convertLocalDateTime(localDate.atStartOfDay());
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(UTC_OFFSET);
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertZonedDateTime(ZonedDateTime zonedDateTime) {
        return convertInstant(zonedDateTime.toInstant());
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertOffsetDateTime(OffsetDateTime offsetDateTime) {
        return convertZonedDateTime(offsetDateTime.atZoneSameInstant(UTC_OFFSET));
    }

    @SuppressWarnings("unused") // Called via reflection
    private static long convertOffsetTime(OffsetTime offsetTime) {
        return convertOffsetDateTime(offsetTime.atDate(LocalDate.now(UTC_ID)));
    }

    /**
     * Converts a {@link Temporal} into it's {@link Long} epoch timestamp equivalent (in seconds) based in UTC.
     *
     * @param temporal the {@link Temporal} to convert
     * @return the {@link Long} epoch timestamp (in seconds) based in UTC
     */
    static long convert(Temporal temporal) {
        String className = temporal.getClass().getSimpleName();
        String methodName = String.format("convert%s", className);
        try {
            return (long) EpochTimestamps.class.getDeclaredMethod(methodName, temporal.getClass())
                    .invoke(null, temporal); // Use null for instance of class (for static methods)
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format(CONVERT_ERR, className), e);
        }
    }

}
