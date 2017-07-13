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

package com.palantir.roboslack.api.attachments.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.VisibleForTesting;
import com.palantir.roboslack.utils.MorePreconditions;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * Represents a block of data rendered at the bottom of a Slack message.
 *
 * @see <a href="https://api.slack.com/docs/message-attachments">Slack Message Attachments</a>
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableFooter.class)
@JsonSerialize(as = ImmutableFooter.class)
public abstract class Footer {

    @VisibleForTesting
    static final int MAX_FOOTER_CHARACTER_LENGTH = 300;

    private static final String TEXT_FIELD = "footer";
    private static final String ICON_FIELD = "footer_icon";
    private static final String TIMESTAMP_FIELD = "ts";

    public static Builder builder() {
        return ImmutableFooter.builder();
    }

    public static Footer of(String text) {
        return builder().text(text).build();
    }

    @Value.Check
    protected final void check() {
        MorePreconditions.checkCharacterLength(TEXT_FIELD, text(), MAX_FOOTER_CHARACTER_LENGTH);
    }

    public interface Builder {
        Builder text(String text);
        Builder icon(URL icon);
        Builder timestamp(long timestamp);
        Footer build();
    }

    /**
     * Text that describes and contextualizes its attachment. <br/>
     * <b>Note:</b> If this text contains any {@link com.palantir.roboslack.api.markdown.SlackMarkdown} special
     * characters, they will be treated as literal plaintext characters when rendered in any Slack client.
     *
     * @return the text
     */
    @JsonProperty(TEXT_FIELD)
    public abstract String text();

    /**
     * The {@link URL} that represents what will be rendered as a small icon beside the {@link Footer#text()}.  The icon
     * gets rendered as a 16px by 16px image, so it's best to use an image that is similarly sized.
     *
     * @return the icon {@link URL}
     */
    @JsonProperty(ICON_FIELD)
    public abstract Optional<URL> icon();

    /**
     * The UNIX epoch timestamp in UTC to append to this {@link Footer}.
     *
     * @return the timestamp in UTC
     */
    @JsonProperty(TIMESTAMP_FIELD)
    public abstract Optional<Long> timestamp();

    /**
     * The {@link LocalDateTime} timestamp of this {@link Footer}, assumed UTC.
     *
     * @return the {@link LocalDateTime} timestamp
     */
    @JsonIgnore
    @Value.Derived
    public Optional<LocalDateTime> timestampLocalDateTime() {
        return timestamp().map(timestamp ->
                LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC));
    }

}
