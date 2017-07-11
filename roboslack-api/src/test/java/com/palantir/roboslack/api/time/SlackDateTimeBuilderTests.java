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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SlackDateTimeBuilderTests {

    static Stream<Executable> invalidFormatTokenConstructors() {
        return Stream.of(
                () -> SlackDateTimeBuilder.of(SlackDateTimeFormat.of(FormatToken.of(""))),
                () -> SlackDateTimeBuilder.of(FormatToken.of(""))
        );
    }

    static Stream<Executable> nullOrEmptyInvalidConstructors() {
        return Stream.of(
                () -> SlackDateTimeBuilder.of(SlackDateTimeFormat.of("")),
                () -> SlackDateTimeBuilder.of(""),
                () -> SlackDateTimeBuilder.of(SlackDateTimeFormat.of(null))
        );
    }

    static Stream<Executable> noFormatTokenConstructors() {
        return Stream.of(
                () -> SlackDateTimeBuilder.of("No format token here"),
                () -> SlackDateTimeBuilder.of(SlackDateTimeFormat.of("{not-a-token}"))
        );
    }

    static Stream<Executable> validConstructors() {
        return Stream.of(
                () -> SlackDateTimeBuilder.of("{date}"),
                () -> SlackDateTimeBuilder.of(FormatToken.DATE_LONG),
                () -> SlackDateTimeBuilder.of(FormatToken.DATE, FormatToken.TIME_SECS),
                () -> SlackDateTimeBuilder.of(
                        SlackDateTimeFormat.of("Written on {date_long_pretty} at {time}"))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidFormatTokenConstructors")
    void testInvalidFormatTokenConstruction(Executable executable) {
        Throwable throwable = assertThrows(IllegalArgumentException.class, executable);
        assertThat(throwable.getMessage(),
                containsString("No Format Token value matching"));
    }

    @ParameterizedTest
    @MethodSource("nullOrEmptyInvalidConstructors")
    void testNullOrEmptyInvalidConstruction(Executable executable) {
        Throwable throwable = assertThrows(IllegalArgumentException.class, executable);
        assertThat(throwable.getMessage(),
                containsString("null or empty"));
    }

    @ParameterizedTest
    @MethodSource("noFormatTokenConstructors")
    void testNoFormatTokenConstruction(Executable executable) {
        Throwable throwable = assertThrows(IllegalArgumentException.class, executable);
        assertThat(throwable.getMessage(),
                containsString("Must contain at least one FormatToken"));
    }

    @ParameterizedTest
    @MethodSource("validConstructors")
    void testValidConstruction(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

}
