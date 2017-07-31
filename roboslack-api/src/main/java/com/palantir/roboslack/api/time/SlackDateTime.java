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

import java.time.temporal.Temporal;

/**
 * Static factory convenience methods for creating formatted date {@link String}s that will be processed by Slack.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @see SlackDateTimeFormat
 * @since 1.0.0
 */
public final class SlackDateTime {

    private SlackDateTime() {}

    public static String create(long epochTimestamp, SlackDateTimeFormat format) {
        return builder(format).build(epochTimestamp);
    }

    public static String create(Temporal temporal, SlackDateTimeFormat format) {
        return builder(format).build(temporal);
    }

    public static String create(long epochTimestamp, String formatPattern) {
        return builder(formatPattern).build(epochTimestamp);
    }

    public static String create(Temporal temporal, String formatPattern) {
        return builder(formatPattern).build(temporal);
    }

    public static String create(long epochTimestamp, DateTimeFormatToken token, DateTimeFormatToken... tokens) {
        return builder(token, tokens).build(epochTimestamp);
    }

    public static String create(Temporal temporal, DateTimeFormatToken token, DateTimeFormatToken... tokens) {
        return builder(token, tokens).build(temporal);
    }

    public static SlackDateTimeBuilder builder(SlackDateTimeFormat format) {
        return SlackDateTimeBuilder.of(format);
    }

    public static SlackDateTimeBuilder builder(String formatPattern) {
        return SlackDateTimeBuilder.of(SlackDateTimeFormat.of(formatPattern));
    }

    public static SlackDateTimeBuilder builder(DateTimeFormatToken token, DateTimeFormatToken... tokens) {
        return SlackDateTimeBuilder.of(SlackDateTimeFormat.of(token, tokens));
    }

}
