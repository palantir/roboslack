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

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.palantir.roboslack.utils.MorePreconditions;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents the formatting pattern that Slack can apply to an epoch timestamp.  It is also used to format the
 * fallback text of a {@link SlackDateTime} before it is sent to Slack.
 *
 * @see java.time.format.DateTimeFormatter
 * @see SlackDateTime
 * @since 1.0.0
 */
public final class SlackDateTimeFormat {

    private static final String PATTERN_EMPTY_ERR = "Pattern cannot be null or empty";
    private static final String FORMAT_TOKENS_ERR = "Must contain at least one FormatToken "
            + "in order to be processed by Slack correctly.";
    private static final Range<Integer> AT_LEAST_ONE = Range.atLeast(1);

    private static final String FORMAT_TOKENS_PATTERN = Joiner.on("|").join(Stream.of(DateTimeFormatToken.values())
            .map(token -> "\\" + token.toString()) // Escape format token literals
            .collect(ImmutableList.toImmutableList()));

    private String pattern;

    private SlackDateTimeFormat(String pattern) {
        checkArgument(!Strings.isNullOrEmpty(pattern), PATTERN_EMPTY_ERR);
        checkArgument(MorePreconditions.containsDateTimeFormatTokens(pattern, AT_LEAST_ONE),
                FORMAT_TOKENS_ERR);
        this.pattern = pattern;
    }

    public static SlackDateTimeFormat of(String pattern) {
        return new SlackDateTimeFormat(pattern);
    }

    public static SlackDateTimeFormat of(DateTimeFormatToken first, DateTimeFormatToken... tokens) {
        List<DateTimeFormatToken> allTokens = ImmutableList.<DateTimeFormatToken>builder()
                .add(first)
                .addAll(Arrays.asList(tokens))
                .build();
        return new SlackDateTimeFormat(Joiner.on(" ").join(allTokens));
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

    private static DateTimeFormatter formatter(String pattern) {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        for (String patternToken : tokenizePattern(pattern)) {
            Optional<DateTimeFormatToken> formatToken = DateTimeFormatToken.ofSafe(patternToken);
            if (formatToken.isPresent()) {
                formatterBuilder.appendPattern(formatToken.get().pattern());
            } else {
                formatterBuilder.appendLiteral(patternToken);
            }
        }
        return formatterBuilder.toFormatter();
    }

    public DateTimeFormatter formatter() {
        return formatter(pattern);
    }

    @Override
    public String toString() {
        return pattern;
    }

}
