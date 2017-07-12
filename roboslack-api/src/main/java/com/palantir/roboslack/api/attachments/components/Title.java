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

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import java.net.URL;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * Represents a Slack message attachment title.
 *
 * @see <a href="https://api.slack.com/docs/message-attachments">Slack Message Attachments</a>
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableTitle.class)
@JsonSerialize(as = ImmutableTitle.class)
public abstract class Title {

    private static final String TEXT_FIELD = "title";
    private static final String LINK_FIELD = "title_link";

    public static Builder builder() {
        return ImmutableTitle.builder();
    }

    public static Title of(String text) {
        return builder().text(text).build();
    }

    @Value.Check
    protected final void check() {
        checkArgument(!Strings.isNullOrEmpty(text()), "The title text field cannot be null or empty");
    }

    /**
     * The title text displayed at the top of the message attachment. Please note that you can pass Markdown
     * characters in this field, but Slack will print them as literal plaintext.
     *
     * @return the title text
     */
    @JsonProperty(TEXT_FIELD)
    public abstract String text();

    /**
     * A valid URL used to hyperlink the title text.
     *
     * @return the URL link
     */
    @JsonProperty(LINK_FIELD)
    public abstract Optional<URL> link();

    public interface Builder {
        Builder text(String text);

        Builder link(URL link);

        Title build();
    }

}
