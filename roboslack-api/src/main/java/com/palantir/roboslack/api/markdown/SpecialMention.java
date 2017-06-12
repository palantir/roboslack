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

import com.google.common.base.Joiner;

/**
 * Special mention types that are supported by Slack.<br/>
 * <b>!channel</b> - alert everyone on a channel, regardless of Away status<br/>
 * <b>!here</b> - alert everyone who is currently Online (not Away) in a channel<br/>
 * <b>!everyone</b> - a synonym for !channel
 *
 * @since 0.1.0
 */
public enum SpecialMention {
    CHANNEL(SlackMarkdown.SPECIAL_MENTION_DECORATION + "channel"),
    HERE(SlackMarkdown.SPECIAL_MENTION_DECORATION + "here"),
    EVERYONE(SlackMarkdown.SPECIAL_MENTION_DECORATION + "everyone");

    private static final String INVALID_MENTION_NAME_FORMAT =
            "No valid SpecialMention type found for '%s', valid values include: [%s]";

    private final String value;

    SpecialMention(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static SpecialMention of(String name) {
        for (SpecialMention mention : values()) {
            if (name.equalsIgnoreCase(mention.toString())) {
                return mention;
            }
        }
        throw new IllegalArgumentException(String.format(INVALID_MENTION_NAME_FORMAT,
                name, Joiner.on(", ").join(values())));
    }

}
