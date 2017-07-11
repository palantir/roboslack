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

package com.palantir.roboslack.utils;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Range;
import com.palantir.roboslack.api.markdown.SlackMarkdown;
import com.palantir.roboslack.api.time.FormatToken;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;

/**
 * Slack messaging comes with several rules on message syntax and formatting. This class checks for these common rules.
 *
 * @since 0.1.0
 */
public final class MorePreconditions {

    private static final String MARKDOWN_ERROR_FORMAT = "The field '%s' cannot contain markdown";
    private static final String CHARACTER_LIMIT_ERROR_FORMAT = "The field '%s' cannot have more than %s characters "
            + "(found %s)";
    private static final String HEX_COLOR_ERROR_FORMAT = "The string '%s' is not a valid hex color value. "
            + "The valid hex color value format is: #??????  (including # symbol).";
    private static final String ONE_PRESENT_AND_VALID_FORMAT = "At least one field should be present and valid: [%s]";
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");

    private MorePreconditions() {
        // Intentionally left blank
    }

    public static void checkCharacterLength(String fieldName, String content, int charLimit) {
        checkArgument(content.length() <= charLimit, CHARACTER_LIMIT_ERROR_FORMAT, fieldName, charLimit,
                content.length());
    }

    public static void checkDoesNotContainMarkdown(String fieldName, String content) {
        checkArgument(!containsMarkdown(content), MARKDOWN_ERROR_FORMAT, fieldName);
    }

    public static void checkHexColor(String value) {
        checkArgument(HEX_COLOR_PATTERN.matcher(value).find(), HEX_COLOR_ERROR_FORMAT, value);
    }

    /**
     * Ensures that at least one field in {code fieldNames} is non-null/empty -- e.g., it is valid.
     *
     * @param fieldNames {@code Collection<String>} field names to check
     * @param optionals {@code }
     */
    public static void checkAtLeastOnePresentAndValid(Collection<String> fieldNames,
            Collection<Optional<String>> optionals) {
        checkArgument(optionals.stream()
                        .anyMatch(optional -> optional.isPresent()
                                && !Strings.isNullOrEmpty(optional.get())),
                ONE_PRESENT_AND_VALID_FORMAT,
                Joiner.on(", ").join(fieldNames));
    }

    /**
     * Returns true if the parameter {@link String} contains any symbols that Slack would process as markdown.
     * Bold, italic, strikethrough, and emojis are tested for in pairs - e.g. one asterisk will not return true, but
     * a pair will.
     *
     * @param text {@link String} for Slack markdown
     * @return {@link boolean} telling us if Slack markdown was found
     */
    public static boolean containsMarkdown(@CheckForNull String text) {
        return text != null && SlackMarkdown.PATTERN.matcher(text).find();
    }

    /**
     * Returns true if the {@code text} contains a summed count of instances of {@link FormatToken} values, based on the
     * sum count {@link Range} {@code acceptanceRange}.
     *
     * @param text the text to count {@link FormatToken} instances on
     * @param acceptanceRange the range threshold
     * @return true if contains accepted count of {@link FormatToken}, false otherwise
     */
    public static boolean containsDateTimeFormatTokens(@CheckForNull String text, Range<Integer> acceptanceRange) {
        return acceptanceRange.contains(Stream.of(FormatToken.values())
                .mapToInt(token -> text.contains(token.toString()) ? 1 : 0)
                .sum());
    }

}
