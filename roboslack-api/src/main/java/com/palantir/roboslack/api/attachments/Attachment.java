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

package com.palantir.roboslack.api.attachments;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.palantir.roboslack.api.attachments.components.Author;
import com.palantir.roboslack.api.attachments.components.Color;
import com.palantir.roboslack.api.attachments.components.Field;
import com.palantir.roboslack.api.attachments.components.Footer;
import com.palantir.roboslack.api.attachments.components.Title;
import com.palantir.roboslack.api.markdown.MarkdownInput;
import com.palantir.roboslack.utils.MorePreconditions;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Represents a full and complete Slack message attachment, with all associated fields.
 *
 * @see <a href="https://api.slack.com/docs/message-attachments">Message Attachments</a>
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableAttachment.class)
@JsonSerialize(as = ImmutableAttachment.class)
public abstract class Attachment {

    /**
     * JSON field names.
     */
    private static final String FALLBACK_FIELD = "fallback";
    private static final String PRETEXT_FIELD = "pretext";
    private static final String IMAGE_URL_FIELD = "image_url";
    private static final String THUMB_URL_FIELD = "thumb_url";
    private static final String MARKDOWN_INPUTS_FIELD = "mrkdwn_in";

    /**
     * Generate a new {@link Attachment.Builder}.
     *
     * @return the newly created {@link Attachment.Builder}
     */
    public static Builder builder() {
        return ImmutableAttachment.builder();
    }

    @Value.Check
    protected final void check() {
        checkArgument(!Strings.isNullOrEmpty(fallback()), "Attachment fallback message cannot be null or empty");
    }

    /**
     * The {@link List} get {@link Field}s for this {@link Attachment}. Fields are displayed in a tabular fashion near
     * the bottom of the {@link Attachment}.
     *
     * @return the {@link List} get {@link Field}s
     */
    @Value.Default
    public List<Field> fields() {
        return ImmutableList.of();
    }

    /**
     * The plaintext summary of this {@link Attachment} used in clients that don't display formatted text. <br/>
     * <b>Note:</b> If this text contains any {@link com.palantir.roboslack.api.markdown.SlackMarkdown} special
     * characters, they will be treated as literal plaintext characters when rendered in any Slack client.
     *
     * @return the {@code fallback} text
     */
    @JsonProperty(FALLBACK_FIELD)
    public abstract String fallback();

    /**
     * The {@link Color} to display on the sidebar next to the {@link Attachment}.
     *
     * @return an {@link Optional} containing a {@link Color}
     * @see Color
     */
    @Value.Default
    @Nullable
    @JsonUnwrapped
    public Color color() {
        return null;
    }

    /**
     * Text that is displayed above the main message of the {@link Attachment} block.
     *
     * @return an {@link Optional} containing the {@code pretext} {@link String}
     */
    @JsonProperty(PRETEXT_FIELD)
    public abstract Optional<String> pretext();

    /**
     * The {@link Author} for this {@link Attachment}. It is displayed in a small grayed-out section at the top
     * before the main {@link Attachment} body.
     *
     * @return an {@link Optional} containing the {@link Author}
     */
    @Value.Default
    @Nullable
    @JsonUnwrapped
    public Author author() {
        return null;
    }

    /**
     * The {@link Title} for this {@link Attachment}. It is displayed as larger, bold text near the top before the main
     * {@link Attachment} body.
     *
     * @return an {@link Optional} containing the {@link Title}
     */
    @Value.Default
    @Nullable
    @JsonUnwrapped
    public Title title() {
        return null;
    }

    /**
     * The main content {@code text} {@link String} of this {@link Attachment}. It can contain {@link
     * com.palantir.roboslack.api.markdown.SlackMarkdown}.
     *
     * @return an {@link Optional} containing the {@code text} {@link String}
     */
    public abstract Optional<String> text();

    /**
     * The {@link URL} referencing an image that is displayed inside the message attachment.  The supported formats
     * are: GIF, JPEG, PNG, and BMP.
     * <p>
     * Large images will be resized to a maximum width of 400px or a maximum height of 500px, while still maintaining
     * the original aspect ratio.
     *
     * @return an {@link Optional} containing the image {@link URL}
     */
    @JsonProperty(IMAGE_URL_FIELD)
    public abstract Optional<URL> imageUrl();

    /**
     * The {@link URL} to an image file that will be displayed as a thumbnail on the left-hand side of a message {@link
     * Attachment}. The supported formats are: GIF, JPEG, PNG, and BMP.
     * <p>
     * The thumbnail's longest dimension will be scaled down to 75px while maintaining the aspect ratio of the image.
     * The filesize of the image must also be less than 500 KB.
     * <p>
     * For best results, use images that are already 75px by 75px.
     *
     * @return an {@link Optional} containing the thumb {@link URL}
     */
    @JsonProperty(THUMB_URL_FIELD)
    public abstract Optional<URL> thumbUrl();

    /**
     * The {@link Footer} for this {@link Attachment}. This will appear below the body of the main message
     * {@link Attachment} in smaller, grayed-out text.
     *
     * @return an {@link Optional} containing the {@link Footer}
     */
    @Value.Default
    @Nullable
    @JsonUnwrapped
    public Footer footer() {
        return null;
    }

    /**
     * A special list of flags that tells Slack where to expect Markdown in an Attachment.
     * Valid values are ["pretext", "text", "fields"].
     *
     * @return the {@link Collection} of {@code markdownInputs}
     */
    @Value.Default
    @JsonProperty(MARKDOWN_INPUTS_FIELD)
    public Set<MarkdownInput> markdownInputs() {
        // inspect the values of the Attachment object and create the mrkdwnIn list.
        ImmutableSet.Builder<MarkdownInput> markdownInputs = ImmutableSet.builder();
        // check if the pretext contains Markdown.
        if (pretext().isPresent() && MorePreconditions.containsMarkdown(pretext().get())) {
            markdownInputs.add(MarkdownInput.PRETEXT);
        }
        // check if the text contains Markdown.
        if (text().isPresent() && MorePreconditions.containsMarkdown(text().get())) {
            markdownInputs.add(MarkdownInput.TEXT);
        }
        // check if any of the Fields' values contain Markdown.
        fields().stream()
                .map(Field::value)
                .filter(MorePreconditions::containsMarkdown)
                .findFirst()
                .ifPresent(ignored -> markdownInputs.add(MarkdownInput.FIELDS));
        return markdownInputs.build();
    }

    public interface Builder {
        Builder fallback(String fallback);

        Builder color(Color color);

        Builder pretext(String pretext);

        Builder author(Author author);

        Builder title(Title title);

        Builder text(String text);

        Builder addFields(Field field);

        Builder addFields(Field... fields);

        Builder fields(Iterable<? extends Field> elements);

        Builder imageUrl(URL imageUrl);

        Builder thumbUrl(URL thumbUrl);

        Builder footer(Footer footer);

        Attachment build();
    }

}
