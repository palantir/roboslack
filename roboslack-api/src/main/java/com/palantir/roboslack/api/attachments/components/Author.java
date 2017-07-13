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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * Represents fields for a Slack Author in an attachment. The author will display in a small section at the top
 * of a message {@link com.palantir.roboslack.api.attachments.Attachment}.
 *
 * @see <a href="https://api.slack.com/docs/message-attachments">Slack Message Attachments</a>
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableAuthor.class)
@JsonSerialize(as = ImmutableAuthor.class)
public abstract class Author {

    private static final String NAME_FIELD = "author_name";
    private static final String LINK_FIELD = "author_link";
    private static final String ICON_FIELD = "author_icon";

    public static Builder builder() {
        return ImmutableAuthor.builder();
    }

    public static Author of(String name) {
        return builder().name(name).build();
    }

    @Value.Check
    protected final void check() {

    }

    /**
     * Small text used to display this {@link Author}'s {@code name}. <br/>
     * <b>Note:</b> If this text contains any {@link com.palantir.roboslack.api.markdown.SlackMarkdown} special
     * characters, they will be treated as literal plaintext characters when rendered in any Slack client.
     *
     * @return the author's name
     */
    @JsonProperty(NAME_FIELD)
    public abstract String name();

    /**
     * A valid {@link URL} that will be applied to the {@link Author#name()}.
     *
     * @return an {@link Optional} containing the link applied to the {@code name} for the {@link Author}
     */
    @JsonProperty(LINK_FIELD)
    public abstract Optional<URL> link();

    /**
     * A valid {@link URL} that referencing a small 16x16px image that is displayed the left of the {@link
     * Author#name()}.
     *
     * @return an {@link Optional} containing the link to the {@code icon} for the {@link Author}
     */
    @JsonProperty(ICON_FIELD)
    public abstract Optional<URL> icon();

    public interface Builder {
        Builder name(String name);

        Builder link(URL link);

        Builder icon(URL icon);

        Author build();
    }

}

