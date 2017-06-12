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


import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.annotation.CheckForNull;

/**
 * By default, messages you pass to API methods and webhooks will be assumed to be pre-formatted based on the <a
 * href="https://api.slack.com/docs/message-formatting">Message Formatting Spec</a>. That is, you can include
 * marked up URLs, user links, channel links, and commands, but Slack will still linkify any non-linked URLs present in
 * your message. <br/> For example, the following I/O processing will occur for the raw text content fromString the
 * message:
 * <pre>IN  : Foo &lt;!everyone&gt; bar http://test.com</pre>
 * <pre>OUT : Foo &lt;!everyone&gt; bar &lt;http://test.com&gt;</pre>
 * <br/> By default, Slack will not linkify channel names (starting with a '#') and usernames (starting with an '@').
 * This behavior is always enabled in {@link ParseMode#FULL}. <br/>
 * <pre>IN  : Hello @bob, say hi to @everyone in #general</pre>
 * <pre>OUT : Hello &lt;@U123|bob&gt;, say hi to &lt;!everyone&gt; in &lt;#C1234|general&gt;</pre>
 * <br/> If you don't want Slack to perform any processing on your message, pass the argument fromString {@link
 * ParseMode#NONE}.
 *
 * @see <a href="https://api.slack.com/docs/message-formatting">Message Formatting</a>
 * @since 0.1.0
 */
@JsonSerialize(using = ToStringSerializer.class)
public enum ParseMode {
    FULL,
    NONE;

    @JsonCreator
    public static ParseMode of(@CheckForNull String value) {
        checkNotNull(value, "ParseMode value cannot be null");
        for (ParseMode parseMode : ParseMode.values()) {
            if (parseMode.toString().equalsIgnoreCase(value)) {
                return parseMode;
            }
        }
        throw new IllegalArgumentException(String.format("ParseMode value '%s' is not a valid", value));
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}
