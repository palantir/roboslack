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
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.palantir.roboslack.utils.MorePreconditions;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class SlackDateTimeFormat {

    private static final String FORMAT_TOKENS_ERR = "Must contain at least one FormatToken "
            + "in order to be processed by Slack correctly.";
    private static final Range<Integer> AT_LEAST_ONE = Range.atLeast(1);

    private static final String FORMAT_TOKENS_SPLIT_PATTERN = String.format("[%s]", Joiner.on("|")
            .join(FormatToken.values()));

    private String pattern;

    private SlackDateTimeFormat(String pattern) {
        checkArgument(MorePreconditions.containsDateTimeFormatTokens(pattern, AT_LEAST_ONE),
                FORMAT_TOKENS_ERR);
        this.pattern = pattern;
    }

    private static List<String> splitPatternOnFormatTokens(String pattern) {
        return Splitter.on(Pattern.compile(FORMAT_TOKENS_SPLIT_PATTERN))
                .omitEmptyStrings()
                .splitToList(pattern);
    }

    private static DateTimeFormatter formatter(String pattern) {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        for (String patternPart : splitPatternOnFormatTokens(pattern)) {
            Optional<FormatToken> formatToken = FormatToken.ofSafe(patternPart);
            if (formatToken.isPresent()) {
                formatterBuilder.appendPattern(formatToken.get().pattern());
            } else {
                formatterBuilder.appendLiteral(patternPart);
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
