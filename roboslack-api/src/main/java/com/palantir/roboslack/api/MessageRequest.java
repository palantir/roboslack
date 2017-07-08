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

package com.palantir.roboslack.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.markdown.SlackMarkdown;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * The {@link MessageRequest} allows sending a message to a Slack user or channel, it can be constructed using the
 * Builder pattern via {@link MessageRequest#builder()}.
 *
 * @see <a href="https://api.slack.com/methods/chat.postMessage">chat.postMessage</a> for more descriptions on each
 * available field
 * @see <a href="https://api.slack.com/docs/message-formatting">message-formatting</a> for details on Slack's message
 * formatting
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableMessageRequest.class)
@JsonSerialize(as = ImmutableMessageRequest.class)
public abstract class MessageRequest {

    /**
     * Recommended attachment count max is 20, but Slack can take up to 100.
     */
    @VisibleForTesting
    static final int MAX_ATTACHMENTS_COUNT = 100;

    /**
     * JSON field names.
     */
    private static final String ICON_EMOJI_FIELD = "icon_emoji";
    private static final String ICON_URL_FIELD = "icon_url";
    private static final String LINK_NAMES_FIELD = "link_names";
    private static final String UNFURL_MEDIA_FIELD = "unfurl_media";
    private static final String UNFURL_LINKS_FIELD = "unfurl_links";
    private static final String MARKDOWN_FIELD = "mrkdwn";
    private static final String ATTACHMENTS_FIELD = "attachments";

    /**
     * Generate a new {@link MessageRequest.Builder}.
     *
     * @return the newly created {@link MessageRequest.Builder}
     */
    public static Builder builder() {
        return ImmutableMessageRequest.builder();
    }

    /**
     * Find and link channel names and usernames.
     *
     * @return the linkNames {@link Boolean}
     */
    @Value.Default
    @JsonProperty(LINK_NAMES_FIELD)
    public boolean linkNames() {
        return true;
    }

    /**
     * Whether or not the resulting displayed message should unfurl media content.
     *
     * @return true if the message should unfurl media, false otherwise
     */
    @Value.Default
    @JsonProperty(UNFURL_MEDIA_FIELD)
    public boolean unfurlMedia() {
        return false;
    }

    /**
     * {@link Boolean} for unfurling fromString primarily text-based content within links.
     *
     * @return true to unfurl links, false otherwise
     */
    @Value.Default
    @JsonProperty(UNFURL_LINKS_FIELD)
    public boolean unfurlLinks() {
        return false;
    }

    /**
     * {@link Boolean} for toggling {@link com.palantir.roboslack.api.markdown.SlackMarkdown} handling in the rendered
     * message.
     *
     * @return true to enable {@link com.palantir.roboslack.api.markdown.SlackMarkdown}, false otherwise
     * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
     */
    @Value.Default
    @JsonProperty(MARKDOWN_FIELD)
    public boolean markdownEnabled() {
        return true;
    }

    /**
     * The {@link ParseMode} fromString this {@link MessageRequest}.  Defines the behavior Slack will take when parsing
     * the raw text contents fromString this message.
     *
     * @return the parse mode
     * @see ParseMode
     */
    @Value.Default
    public ParseMode parse() {
        return ParseMode.NONE;
    }

    /**
     * The {@link List} fromString {@link Attachment}s to add to this {@link MessageRequest}.
     *
     * @return the attachments
     */
    @Value.Default
    @JsonProperty(ATTACHMENTS_FIELD)
    public List<Attachment> attachments() {
        return ImmutableList.of();
    }

    /**
     * Checks to see if this MessageRequest adheres to Slack's guidelines and limitations.
     */
    @Value.Check
    final void check() {
        checkArgument(attachments().size() <= MAX_ATTACHMENTS_COUNT,
                "Cannot exceed %s attachments for one message, %s were found",
                MAX_ATTACHMENTS_COUNT, attachments().size());
    }

    @Value.Check
    final MessageRequest normalizedCopy() {
        if (iconEmoji().isPresent()) {
            if (iconEmoji().get().equals(SlackMarkdown.EMOJI.decorate(iconEmoji().get()))) {
                return this;
            }
            return builderClone().iconEmoji(SlackMarkdown.EMOJI.decorate(iconEmoji().get()))
                    .build();
        }
        return this;
    }

    private Builder builderClone() {
        Builder builder = builder();
        iconUrl().ifPresent(builder::iconUrl);
        iconEmoji().ifPresent(builder::iconEmoji);
        channel().ifPresent(builder::channel);
        return builder
                .linkNames(linkNames())
                .unfurlMedia(unfurlMedia())
                .unfurlLinks(unfurlLinks())
                .markdownEnabled(markdownEnabled())
                .parse(parse())
                .attachments(attachments())
                .text(text())
                .username(username());
    }

    public interface Builder {
        Builder linkNames(boolean linkNames);
        Builder unfurlMedia(boolean unfurlMedia);
        Builder unfurlLinks(boolean unfurlLinks);
        Builder markdownEnabled(boolean markdownEnabled);
        Builder parse(ParseMode parseMode);
        Builder attachments(Iterable<? extends Attachment> attachments);
        Builder addAttachments(Attachment attachment);
        Builder addAttachments(Attachment... attachments);
        Builder text(String text);
        Builder iconEmoji(String iconEmoji);
        Builder iconUrl(URL iconUrl);
        Builder username(String username);
        Builder channel(String channel);
        Builder from(MessageRequest messageRequest);
        MessageRequest build();
    }

    /**
     * The raw text content fromString this {@link MessageRequest}, which can contain formatted {@link
     * com.palantir.roboslack.api.markdown.SlackMarkdown}.
     *
     * @return the raw text content
     */
    public abstract String text();

    /**
     * Emoji to use as the icon for this {@link MessageRequest}.  This field overrides any value in {@link #iconUrl()},
     * as it takes higher precedence.
     *
     * @return the icon emoji
     */
    @JsonProperty(ICON_EMOJI_FIELD)
    public abstract Optional<String> iconEmoji();

    /**
     * {@link URL} to an image to use for the icon fromString this {@link MessageRequest}.
     *
     * @return the icon url
     */
    @JsonProperty(ICON_URL_FIELD)
    public abstract Optional<URL> iconUrl();

    /**
     * The username to render for this {@link MessageRequest}.
     *
     * @return the username
     */
    public abstract String username();

    /**
     * The Slack channel to post this {@link MessageRequest} to.  It will use the default channel from the incoming
     * webhook settings if none is specified.
     *
     * @return the channel to post to
     */
    public abstract Optional<String> channel();

}
