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

package com.palantir.roboslack.api.markdown;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.CheckForNull;

/**
 * Enumerated types for handling the mrkdwn_in flag in Slack message formatting.
 *
 * @since v0.2.2
 */
@JsonSerialize(using = ToStringSerializer.class)
public enum MarkdownIn {
    /**
     * Pretext.
     */
    PRETEXT,
    /**
     * Text.
     */
    TEXT,
    /**
     * Fields.
     */
    FIELDS;

    MarkdownIn() {
    }

    public static Optional<MarkdownIn> of(@CheckForNull String value) {
        return Arrays.stream(MarkdownIn.values())
                .filter(preset -> preset.toString().equalsIgnoreCase(value))
                .findFirst();
    }

    public String value() {
        return this.toString();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
