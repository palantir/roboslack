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


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

class EpochTimestampConverterTests {

    private static final long EXACT_EXAMPLE = 1392734382L; // 2014-02-18 6:39:42 AM

    private static final Map<Temporal, Long> CONVERSION_FIXTURES = ImmutableMap.<Temporal, Long>builder()
            // Exact conversions
            .put(Instant.ofEpochSecond(EXACT_EXAMPLE), EXACT_EXAMPLE)
            .put(LocalDateTime.ofEpochSecond(EXACT_EXAMPLE, 0, ZoneOffset.UTC), EXACT_EXAMPLE)
            .put(ZonedDateTime.ofInstant(Instant.ofEpochSecond(EXACT_EXAMPLE), ZoneOffset.UTC), EXACT_EXAMPLE)
            .put(OffsetDateTime.ofInstant(Instant.ofEpochSecond(EXACT_EXAMPLE), ZoneOffset.UTC), EXACT_EXAMPLE)
            // Loose conversions (granularity inferred)
            .put(LocalDate.of(2014, 2, 18), 1392681600L)
            .put(LocalTime.of(6, 39, 42, 0), 10046382L)
            .put(OffsetTime.of(LocalTime.of(6, 39, 42, 0), ZoneOffset.UTC), 10046382L)
            .build();

    @ParameterizedTest
    @ArgumentsSource(ConversionFixturesProvider.class)
    void testReflectiveConvert(Map.Entry<Temporal, Long> fixture) {
        assertEquals(EpochTimestampConverter.convert(fixture.getKey()), fixture.getValue().longValue());
    }

    private static class ConversionFixturesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return CONVERSION_FIXTURES.entrySet().stream().map(Arguments::of);
        }

    }

}
