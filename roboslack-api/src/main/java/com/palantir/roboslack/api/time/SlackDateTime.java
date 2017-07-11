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
 * Static utility methods for creating formatted date {@link String}s.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @see SlackDateTimeFormat
 * @since 1.0.0
 */
public final class SlackDateTime {

    private SlackDateTime() {}

    public String create(SlackDateTimeFormat format, long epochTimestamp) {
        return SlackDateTimeBuilder.of(format).build(epochTimestamp);
    }

    public String create(SlackDateTimeFormat format, Temporal temporal) {
        return SlackDateTimeBuilder.of(format).build(temporal);
    }

}
