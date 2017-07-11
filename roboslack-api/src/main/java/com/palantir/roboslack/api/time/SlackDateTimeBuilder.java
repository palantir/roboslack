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

import com.palantir.roboslack.api.markdown.StringDecorator;
import com.palantir.roboslack.api.markdown.ValueDecorator;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.Temporal;

/**
 * This class represents a reusable builder container for a {@link SlackDateTimeFormat} that can be applied to epoch
 * timestamp inputs.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @see SlackDateTimeFormat
 * @since 1.0.0
 */
public final class SlackDateTimeBuilder {

    private static final ValueDecorator<String> OUTPUT_DECORATOR = StringDecorator.of("<!date", ">");

    private static final String OUTPUT_WITH_LINK_FORMAT = "^%08d^%s^%s|%s";
    private static final String OUTPUT_WITHOUT_LINK_FORMAT = "^%08d^%s|%s";

    private final SlackDateTimeFormat format;

    private SlackDateTimeBuilder(SlackDateTimeFormat format) {
        this.format = format;
    }

    public static SlackDateTimeBuilder of(SlackDateTimeFormat format) {
        return new SlackDateTimeBuilder(format);
    }

    public static SlackDateTimeBuilder of(String pattern) {
        return of(SlackDateTimeFormat.of(pattern));
    }

    public static SlackDateTimeBuilder of(FormatToken token, FormatToken... tokens) {
        return of(SlackDateTimeFormat.of(token, tokens));
    }

    private static String computeFallbackText(SlackDateTimeFormat format, long epochTimestamp) {
        Instant instant = Instant.ofEpochSecond(epochTimestamp);
        return format.formatter().format(instant);
    }

    public String build(long epochTimestamp) {
        return OUTPUT_DECORATOR.decorate(
                String.format(OUTPUT_WITHOUT_LINK_FORMAT,
                        epochTimestamp,
                        format,
                        computeFallbackText(format, epochTimestamp)));
    }

    public String build(long epochTimestamp, URL link) {
        return OUTPUT_DECORATOR.decorate(
                String.format(OUTPUT_WITH_LINK_FORMAT,
                        epochTimestamp,
                        format,
                        link.toString(),
                        computeFallbackText(format, epochTimestamp)));
    }

    public String build(Temporal temporal) {
        return build(EpochTimestampConverter.convert(temporal));
    }

    public String build(Temporal temporal, URL link) {
        return build(EpochTimestampConverter.convert(temporal), link);
    }

}
