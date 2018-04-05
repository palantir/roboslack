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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SlackDateTimeFormatterTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "-",
            "Something without tokens",
            "{not-a-token}",
            "{}",
            "{date",
            "time}"})
    void testInvalidConstruction(String pattern) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> SlackDateTimeFormatter.of(pattern));
        assertThat(thrown.getMessage(), containsString("at least one FormatToken"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{date}",
            "Processed on: {date}",
            "{time} on {date_long_pretty}",
            "{time_secs}",
            "{date_long}T{time_secs}"})
    void testValidConstruction(String pattern) {
        SlackDateTimeFormatter formatter = SlackDateTimeFormatter.of(pattern);
        assertValid(formatter);
    }

    @Test
    void testValidConstructionForTokens() {
        SlackDateTimeFormatter dateFormatter = SlackDateTimeFormatter.of(DateTimeFormatToken.DATE);
        assertValid(dateFormatter);

        SlackDateTimeFormatter dateAndTimeFormatter = SlackDateTimeFormatter.of(DateTimeFormatToken.DATE_LONG_PRETTY,
                DateTimeFormatToken.TIME_SECS);
        assertValid(dateAndTimeFormatter);
    }

    private void assertValid(SlackDateTimeFormatter formatter) {
        assertFalse(Strings.isNullOrEmpty(formatter.toString()));
        assertFalse(Strings.isNullOrEmpty(formatter.pattern()));
    }

}
