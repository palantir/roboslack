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

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.palantir.roboslack.utils.MorePreconditions;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.immutables.value.Value;

/**
 * Represents the formatting pattern that Slack can apply to an epoch timestamp.  It is also used to format the fallback
 * text of a {@link SlackDateTime} before it is sent to Slack.
 *
 * @see java.time.format.DateTimeFormatter
 * @see SlackDateTime
 * @since 1.0.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableSlackDateTimeFormatter.class)
@JsonSerialize(as = ImmutableSlackDateTimeFormatter.class)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public abstract class SlackDateTimeFormatter {

    private static final String PATTERN_EMPTY_ERR = "Pattern cannot be null or empty";
    private static final String FORMAT_TOKENS_ERR = "Must contain at least one FormatToken "
            + "in order to be processed by Slack correctly.";
    private static final Range<Integer> AT_LEAST_ONE = Range.atLeast(1);

    private static final String FORMAT_TOKENS_PATTERN = Joiner.on("|").join(Stream.of(DateTimeFormatToken.values())
            .map(token -> "\\" + token.toString()) // Escape format token literals
            .collect(ImmutableList.toImmutableList()));
    private static final Joiner DEFAULT_MULTI_TOKEN_JOINER = Joiner.on(" ");

    /**
     * The date-time formatter that formats a date-time without an offset, in the form of 'yyyy-mm-dd kk:mm:ss'.
     */
    public static final SlackDateTimeFormatter LOCAL_DATE_TIME =
            of(DateTimeFormatToken.DATE_NUM, DateTimeFormatToken.TIME_SECS);

    /**
     * Creates a new {@link SlackDateTimeFormatter} using the provided pattern.  The pattern can contain any permutation
     * of the {@link String} representations of {@link DateTimeFormatToken}, with any other text formatting.
     *
     * @param pattern the {@link String} pattern
     * @return the new {@link SlackDateTimeFormatter}
     */
    public static SlackDateTimeFormatter of(String pattern) {
        return ImmutableSlackDateTimeFormatter.builder()
                .pattern(pattern)
                .build();
    }

    /**
     * Creates a new {@link SlackDateTimeFormatter} using the provided {@link DateTimeFormatToken}s.  If multiple tokens
     * are provided, then the formatter will delimit each token with a single space (" ") character.
     *
     * @param token the {@link DateTimeFormatToken} to use for the formatter
     * @param tokens any additional {@link DateTimeFormatToken}s for the formatter
     * @return the new {@link SlackDateTimeFormatter}
     */
    public static SlackDateTimeFormatter of(DateTimeFormatToken token, DateTimeFormatToken... tokens) {
        List<DateTimeFormatToken> allTokens = ImmutableList.<DateTimeFormatToken>builder()
                .add(token)
                .addAll(Arrays.asList(tokens))
                .build();
        return of(DEFAULT_MULTI_TOKEN_JOINER.join(allTokens));
    }

    private static List<String> tokenizePattern(String pattern) {
        ImmutableList.Builder<String> tokens = ImmutableList.builder();
        Matcher matcher = Pattern.compile(FORMAT_TOKENS_PATTERN, Pattern.CASE_INSENSITIVE).matcher(pattern);
        int currentIndex = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > currentIndex) {
                tokens.add(pattern.substring(currentIndex, start));
                tokens.add(pattern.substring(start, end));
            } else {
                tokens.add(pattern.substring(start, end));
            }
            currentIndex = end;
        }
        return tokens.build();
    }

    @Value.Check
    protected final void check() {
        checkArgument(!Strings.isNullOrEmpty(pattern()), PATTERN_EMPTY_ERR);
        checkArgument(MorePreconditions.containsDateTimeFormatTokens(pattern(), AT_LEAST_ONE), FORMAT_TOKENS_ERR);
    }

    @Override
    public final String toString() {
        return pattern();
    }

    /**
     * The delegate {@link DateTimeFormatter} for formatting a {@link SlackDateTime} outside of the context of Slack.
     *
     * @return the {@link DateTimeFormatter} to use when formatting a {@link SlackDateTime} outside of Slack
     */
    @Value.Derived
    protected DateTimeFormatter delegateFormatter() {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        for (String patternToken : tokenizePattern(pattern())) {
            Optional<DateTimeFormatToken> formatToken = DateTimeFormatToken.ofSafe(patternToken);
            if (formatToken.isPresent()) {
                formatterBuilder.appendPattern(formatToken.get().pattern());
            } else {
                formatterBuilder.appendLiteral(patternToken);
            }
        }
        return formatterBuilder.toFormatter();
    }

    /**
     * Formats the provided {@link TemporalAccessor} into it's {@link String} representation using this {@link
     * SlackDateTimeFormatter}'s underlying pattern. <p> <b>NOTE: This does not create a {@link String} that
     * is dynamically render-able on Slack. This is intended to provide a means to apply the same formatting from
     * this {@link SlackDateTimeFormatter} to a {@link java.time.temporal.Temporal} outside of the context of
     * Slack or any Slack client.</b></p>
     *
     * @param temporal the {@link TemporalAccessor} to format
     * @return the {@link String} representation of the {@link TemporalAccessor}
     */
    public String format(TemporalAccessor temporal) {
        return delegateFormatter().format(temporal);
    }

    /**
     * Convenience method that formats the provided {@link SlackDateTime} into it's {@link String} representation using
     * this {@link SlackDateTimeFormatter}'s underlying pattern.  This will create a {@link String} that
     * renders dynamically on Slack clients. <p> NOTE: This is equivalent to call
     * {@link SlackDateTime#format(SlackDateTimeFormatter)} using this {@link SlackDateTimeFormatter} instance. </p>
     *
     * @param slackDateTime the {@link SlackDateTime} to format
     * @return the {@link String} representation of this {@link SlackDateTime} that renders dynamically on Slack clients
     */
    public String format(SlackDateTime slackDateTime) {
        return slackDateTime.format(this);
    }

    /**
     * Gets the {@link String} representation of the pattern used by this {@link SlackDateTimeFormatter}.
     *
     * @return the {@link String} pattern
     */
    public abstract String pattern();

}
