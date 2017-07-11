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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.palantir.roboslack.api.markdown.StringDecorator;
import com.palantir.roboslack.api.markdown.ValueDecorator;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;

/**
 * Enumerated types for describing date and time formats in the way Slack expects.
 * All format descriptions are provided using the {@link java.time.format.DateTimeFormatter} symbol designations.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @see java.time.format.DateTimeFormatter
 * @since 1.0.0
 */
@JsonSerialize(using = ToStringSerializer.class)
public enum FormatToken {
    /**
     * {@code MMMM dd(th/st/nd/rd), yyyy} <br/>
     * (ie. {@code February 18th, 2014}).
     */
    DATE("MMMMM dd, yyyy"),
    /**
     * {@code yyyy-mm-dd} <br/>
     * (ie. {@code 2014-02-18})
     */
    DATE_NUM("yyyy-mm-dd"),
    /**
     * {@code MMM dd, yyyy} <br/>
     * (ie. {@code Feb 18, 2014})
     */
    DATE_SHORT("MMM dd, yyyy"),
    /**
     * {@code eeee, MMMMM dd(th/st/nd/rd), yyyy}
     * (ie. {@code Tuesday, February 18th, 2014})
     */
    DATE_LONG("eeee, MMMM dd, yyyy"),
    /**
     * Same as {@link #DATE} but uses "yesterday", "today", or "tomorrow" where appropriate.
     */
    DATE_PRETTY("MMMM dd, yyyy"), // Delegate to just using DATE's pattern
    /**
     * Same as {@link #DATE_SHORT} but uses "yesterday", "today", or "tomorrow" where appropriate.
     */
    DATE_SHORT_PRETTY("MMM dd, yyyy"), // Delegate to just using DATE_SHORT's pattern
    /**
     * Same as {@link #DATE_LONG} but uses "yesterday", "today", or "tomorrow" where appropriate.
     */
    DATE_LONG_PRETTY("eeee, MMMMM dd, yyyy"), // Delegate to just using DATE_LONG's pattern
    /**
     * If client is set to show 12hr format: {@code hh:mm a} <br/>
     * (ie. {@code 6:39 AM} or {@code 6:39 PM}) <br/>
     * If client is set to show 24hr format: {@code kk:mm} <br/>
     * (ie. {@code 06:39} or {@code 18:39}) <br/>
     */
    TIME("hh:mm a"), // Default to 12hr format
    /**
     * If client is set to show 12hr format: {@code hh:mm:ss a} <br/>
     * (ie. {@code 6:39:45 AM} or {@code 6:39:42 PM}) <br/>
     * If client is set to show 24hr format: {@code kk:mm:ss} <br/>
     * (ie. {@code 06:39:45} or {@code 18:39:42}) <br/>
     */
    TIME_SECS("hh:mm:ss a"); // Default to 12hr format

    private static final ValueDecorator<String> TOKEN_DECORATOR = StringDecorator.of("{", "}");

    private static final String NOT_FOUND_ERR = "No Format Token value matching: %s";

    private String pattern;

    FormatToken(String pattern) {
        this.pattern = pattern;
    }

    public static Optional<FormatToken> ofSafe(@CheckForNull String input) {
        return Stream.of(values())
                .filter(token -> token.toString().equalsIgnoreCase(input))
                .findFirst();
    }

    @JsonCreator
    public static FormatToken of(@CheckForNull String input) {
        return ofSafe(input)
                .orElseThrow(() -> new IllegalArgumentException(String.format(NOT_FOUND_ERR, input)));
    }

    @Override
    public String toString() {
        return TOKEN_DECORATOR.decorate(name().toLowerCase());
    }

    public String pattern() {
        return pattern;
    }

}
