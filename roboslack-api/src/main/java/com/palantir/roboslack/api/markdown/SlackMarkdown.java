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

import java.net.URL;
import java.util.regex.Pattern;

/**
 * The main entry point class for generating Slack markdown text.
 *
 * @since 0.1.0
 */
public final class SlackMarkdown {

    /**
     * Generic {@link Pattern} that matches any {@link SlackMarkdown}.
     */
    public static final Pattern PATTERN =
            Pattern.compile("(@|#|!|\\*.+\\*|~.+~|_.+_|:.+:|-.+-|\n|`.+`|>{3}|%E2%80%A2)");

    /**
     * Slack's required encoded newline separator for messages.
     */
    public static final String NEWLINE_SEPARATOR = "\n";

    /**
     * Enumerated decoration {@link String}s, usually used as a prefix and suffix for a {@link StringDecorator}.
     */
    public static final String BOLD_DECORATION = "*";
    public static final String ITALIC_DECORATION = "_";
    public static final String STRIKE_DECORATION = "~";
    public static final String EMOJI_DECORATION = ":";
    public static final String PREFORMAT_DECORATION = "`";
    public static final String PREFORMAT_MULTILINE_DECORATION = "```";
    public static final String SPECIAL_MENTION_DECORATION = "!";

    public static final String MENTION_USER_PREFIX = "@";
    public static final String MENTION_CHANNEL_PREFIX = "#";
    public static final String QUOTE_PREFIX = ">";
    public static final String QUOTE_MULTILINE_PREFIX = ">>>";
    public static final String LINK_PREFIX = "<";
    public static final String LINK_SUFFIX = ">";
    public static final String LINK_TEXT_SEPARATOR = "|";
    public static final String LIST_BULLET_PREFIX = "• ";

    /**
     * Slack Markdown decorators, which provide a convenience layer for generating properly-formatted Slack Markdown.
     */
    public static final ValueDecorator<String> BOLD = StringDecorator.of(BOLD_DECORATION);
    public static final ValueDecorator<String> ITALIC = StringDecorator.of(ITALIC_DECORATION);
    public static final ValueDecorator<String> STRIKE = StringDecorator.of(STRIKE_DECORATION);
    public static final ValueDecorator<String> EMOJI = StringDecorator.of(EMOJI_DECORATION);
    public static final ValueDecorator<String> PREFORMAT = StringDecorator.of(PREFORMAT_DECORATION);
    public static final ValueDecorator<String> PREFORMAT_MULTILINE = StringDecorator.of(
            PREFORMAT_MULTILINE_DECORATION + NEWLINE_SEPARATOR, PREFORMAT_MULTILINE_DECORATION);
    public static final ValueDecorator<String> MENTION_USER = StringDecorator.ofPrefix(MENTION_USER_PREFIX);
    public static final ValueDecorator<String> MENTION_CHANNEL = StringDecorator.ofPrefix(MENTION_CHANNEL_PREFIX);
    public static final ValueDecorator<String> QUOTE = StringDecorator.of(NEWLINE_SEPARATOR + QUOTE_PREFIX,
            NEWLINE_SEPARATOR);
    public static final ValueDecorator<String> QUOTE_MULTILINE = StringDecorator.of(
            QUOTE_MULTILINE_PREFIX + NEWLINE_SEPARATOR, NEWLINE_SEPARATOR);
    public static final ValueDecorator<String> NEWLINE = StringDecorator.ofSuffix(NEWLINE_SEPARATOR);
    public static final TupleDecorator<URL, String> LINK = LinkDecorator
            .of(LINK_PREFIX, LINK_TEXT_SEPARATOR, LINK_SUFFIX);
    public static final ValueDecorator<String> LIST_SINGLE_LEVEL = StringDecorator.of(LIST_BULLET_PREFIX,
            NEWLINE_SEPARATOR);

    private SlackMarkdown() {
        // left blank intentionally
    }

}

