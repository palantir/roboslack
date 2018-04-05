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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.palantir.roboslack.api.markdown.StringDecorator;
import com.palantir.roboslack.api.markdown.ValueDecorator;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * This class represents a {@link Temporal} object that can be dynamically rendered on a Slack client. <p> For example,
 * if the client views a {@link SlackDateTime} with Date granularity (that represents a point in time at the same day of
 * the client) Slack will render it as {@code today}, but when they view the same message again the following day, Slack
 * will render the message as {@code yesterday} instead. </p>
 * <p>
 * Any {@link Temporal} object alone doesn't have this advantage when being formatting to a {@link String} 'statically',
 * and it also doesn't respect clients' timezones.  Whereas if a client crosses between timezones and views the same
 * formatted message of the {@link SlackDateTime} again, it will be altered into their new location's timezone.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @see SlackDateTimeFormatter
 * @since 1.0.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableSlackDateTime.class)
@JsonSerialize(as = ImmutableSlackDateTime.class)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public abstract class SlackDateTime {

    /**
     * The default format for rendering any {@link SlackDateTime}.
     */
    public static final SlackDateTimeFormatter DEFAULT_FORMAT = SlackDateTimeFormatter.LOCAL_DATE_TIME;

    private static final ValueDecorator<String> OUTPUT_DECORATOR = StringDecorator.of("<!date", ">");

    private static final String OUTPUT_WITH_LINK_FORMAT = "^%08d^%s^%s|%s";
    private static final String OUTPUT_WITHOUT_LINK_FORMAT = "^%08d^%s|%s";

    /**
     * Creates a new {@link SlackDateTime} based on the epoch timestamp provided (in seconds).
     *
     * @param epochTimestamp the epochTimestamp in seconds
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(long epochTimestamp) {
        return ImmutableSlackDateTime.builder().epochTimestamp(epochTimestamp).build();
    }

    /**
     * Creates a new {@link SlackDateTime} of the provided epoch timestamp (in seconds), and a {@link URL} that will be
     * rendered as a hyperlink reference within a Slack client.
     *
     * @param epochTimestamp the epochTimestamp in seconds
     * @param link the {@link URL} that will be rendered as a hyperlink reference within a Slack client
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(long epochTimestamp, URL link) {
        return ImmutableSlackDateTime.builder().epochTimestamp(epochTimestamp).link(link).build();
    }

    /**
     * Creates a new {@link SlackDateTime} of the provided epoch timestamp (in seconds), and a {@link String} link (in
     * {@link URL} format) that will be rendered as a hyperlink reference within a Slack client.
     *
     * @param epochTimestamp the epochTimestamp in seconds
     * @param link the {@link String} (in {@link URL} format) that will be rendered as a hyperlink reference within a
     * Slack client
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(long epochTimestamp, String link) {
        URL url;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Link is malformed: %s", link), e);
        }
        return of(epochTimestamp, url);
    }

    /**
     * Creates a new {@link SlackDateTime} of the provided {@link Temporal}.
     *
     * @param temporal the {@link Temporal}
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(Temporal temporal) {
        return of(EpochTimestamps.convert(temporal));
    }

    /**
     * Creates a new {@link SlackDateTime} of the provided {@link Temporal}, and a {@link URL} that will be rendered as
     * a hyperlink reference within a Slack client.
     *
     * @param temporal the {@link Temporal}
     * @param link the {@link URL} that will be rendered as a hyperlink reference within a Slack client
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(Temporal temporal, URL link) {
        return of(EpochTimestamps.convert(temporal), link);
    }

    /**
     * Creates a new {@link SlackDateTime} of the provided {@link Temporal}, and a {@link String} link (in {@link URL}
     * format) that will be rendered as a hyperlink reference within a Slack client.
     *
     * @param temporal the {@link Temporal}
     * @param link the {@link String} (in {@link URL} format) that will be rendered as a hyperlink reference within a
     * Slack client
     * @return the new {@link SlackDateTime}
     */
    public static SlackDateTime of(Temporal temporal, String link) {
        return of(EpochTimestamps.convert(temporal), link);
    }

    private String computeFallbackText(SlackDateTimeFormatter formatter) {
        return formatter.format(LocalDateTime.ofEpochSecond(epochTimestamp(), 0, ZoneOffset.UTC));
    }

    /**
     * Formats this {@link SlackDateTime} using the provided {@link SlackDateTimeFormatter}.
     * This will create a {@link String} that renders dynamically on Slack clients. <p> This formats the {@link
     * SlackDateTime} to a {@link String} using the rules of the formatter. </p>
     *
     * @param formatter the {@link SlackDateTimeFormatter} to use
     * @return the {@link String} representation of this {@link SlackDateTime} that renders dynamically on Slack clients
     */
    public final String format(SlackDateTimeFormatter formatter) {
        if (link().isPresent()) {
            return OUTPUT_DECORATOR.decorate(
                    String.format(OUTPUT_WITH_LINK_FORMAT,
                            epochTimestamp(),
                            formatter.pattern(),
                            link().get().toString(),
                            computeFallbackText(formatter)));
        } else {
            return OUTPUT_DECORATOR.decorate(
                    String.format(OUTPUT_WITHOUT_LINK_FORMAT,
                            epochTimestamp(),
                            formatter.pattern(),
                            computeFallbackText(formatter)));
        }
    }

    /**
     * Formats this {@link SlackDateTime} with the default format of {@link SlackDateTimeFormatter#LOCAL_DATE_TIME},
     * which is the same as calling {@link #format(SlackDateTimeFormatter.LOCAL_DATE_TIME)}.
     *
     * @return the {@link String} representation of this {@link SlackDateTime} that renders dynamically on Slack clients
     */
    public final String format() {
        return format(DEFAULT_FORMAT);
    }

    /**
     * Formats this {@link SlackDateTime} with the default format of {@link SlackDateTimeFormatter#LOCAL_DATE_TIME},
     * which is the same as calling {@link #format(SlackDateTimeFormatter.LOCAL_DATE_TIME)}.
     *
     * @return the {@link String} representation of this {@link SlackDateTime} that renders dynamically on Slack clients
     */
    @Override
    public String toString() {
        return format();
    }

    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     */
    public abstract long epochTimestamp();

    /**
     * Gets the link to render within the Slack client when this date is formatted.
     *
     * @return an {@link Optional} containing the {@link URL}
     */
    public abstract Optional<URL> link();

}
