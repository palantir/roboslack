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

import com.google.common.collect.ImmutableMap;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class SlackDateTimeTests {

    private static final SlackDateTimeFormatter EXAMPLE_FORMATTER =
            SlackDateTimeFormatter.of(DateTimeFormatToken.DATE_LONG_PRETTY);

    private static final String EXAMPLE_URL = "https://www.palantir.com";

    private Stream<Map.Entry<Long, String>> constructorsOfTimestamp() {
        return ImmutableMap.of(
                1234L, "<!date^00001234^"
        ).entrySet().stream();
    }

    private Stream<Map.Entry<Temporal, String>> constructorsOfTemporal() {
        return ImmutableMap.of(
                Temporal.class.cast(OffsetDateTime
                        .of(2018, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)), ""
        ).entrySet().stream();
    }

    @Test
    void testConstructWithTimestamp() {

    }

}
