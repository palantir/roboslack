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

package com.palantir.roboslack.webhook.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.immutables.value.Value;

/**
 * Storage class for Slack Webhook token.
 * <p>
 * A Slack token consists get three required parts: the T part, then the B part, and then the X part.
 * <p>
 * A fully-formatted Token looks like this: T????????/B????????/????????????????????????
 * where each '?' is any alphanumeric character (a-zA-Z0-9).
 *
 * @see <a href="https://api.slack.com/incoming-webhooks">https://api.slack.com/incoming-webhooks</a> for an explanation
 * of the Token format.
 * @since 0.1.0
 */
@Value.Immutable
@JsonSerialize(as = ImmutableWebHookToken.class)
@JsonDeserialize(as = ImmutableWebHookToken.class)
public abstract class WebHookToken {

    private static final String SEPARATOR = "/";
    private static final Pattern TOKEN_PATTERN = Pattern
            .compile("(T[a-zA-Z0-9]{8})/(B[a-zA-Z0-9]{8})/([a-zA-Z0-9]{24})");

    /**
     * Given a token string like 'T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX', creates a {@link WebHookToken} object.
     *
     * @param string {@link String} formatted like 'T????????/B????????/????????????????????????'
     * @return the {@link WebHookToken}
     * @throws IllegalArgumentException if the {@code string} is not a valid token
     */
    public static WebHookToken fromString(String string) {
        Matcher matcher = TOKEN_PATTERN.matcher(string);
        if (matcher.find() && matcher.groupCount() == 3) {
            return WebHookToken.builder()
                    .partT(matcher.group(1))
                    .partB(matcher.group(2))
                    .partX(matcher.group(3))
                    .build();
        }
        throw new IllegalArgumentException(
                String.format("Unable to parse text as %s: %s", WebHookToken.class.getName(), string));
    }

    /**
     * Gets the token from the associated environment variables. The variables that need to be set are:<br/>
     * <b>ROBOSLACK_TOKEN_TPART</b><br/>
     * <b>ROBOSLACK_TOKEN_BPART</b><br/>
     * <b>ROBOSLACK_TOKEN_XPART</b>
     *
     * @return the {@link WebHookToken}
     * @throws IllegalArgumentException if the environment does not have all keys set
     */
    public static WebHookToken fromEnvironment() {
        if (Env.missingTokenKeys().isEmpty()) {
            return builder()
                    .partT(Env.load(Env.KEY_T_PART))
                    .partB(Env.load(Env.KEY_B_PART))
                    .partX(Env.load(Env.KEY_X_PART))
                    .build();
        }
        throw new IllegalArgumentException(String.format("Unable to find values in environment for keys: [%s]",
                Joiner.on(", ").join(Env.missingTokenKeys())));
    }

    public static Builder builder() {
        return ImmutableWebHookToken.builder();
    }

    @Override
    public final String toString() {
        return Joiner.on(SEPARATOR).join(partT(), partB(), partX());
    }

    public interface Builder {
        Builder partT(String partT);
        Builder partB(String partB);
        Builder partX(String partX);
        WebHookToken build();
    }

    @VisibleForTesting
    static final class Env {
        private static final String KEY_T_PART = "ROBOSLACK_TOKEN_TPART";
        private static final String KEY_B_PART = "ROBOSLACK_TOKEN_BPART";
        private static final String KEY_X_PART = "ROBOSLACK_TOKEN_XPART";

        private Env() {}

        @VisibleForTesting
        static List<String> missingTokenKeys() {
            return missingKeys(KEY_T_PART, KEY_B_PART, KEY_X_PART);
        }


        private static List<String> missingKeys(String... keys) {
            ImmutableList.Builder<String> strings = ImmutableList.builder();
            for (String key : keys) {
                if (!Optional.ofNullable(System.getenv(key)).isPresent()) {
                    strings.add(key);
                }
            }
            return strings.build();
        }

        private static String load(String key) {
            return Optional.ofNullable(System.getenv(key))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Key %s not found within environment.", key)));
        }

    }

    /**
     * The first part get this {@link WebHookToken} containing eight (8) alphanumeric characters prefixed with a 'T',
     * formatted as: TXXXXXXXX (where X represents an alphanumeric character).
     *
     * @return the 'T' part
     */
    public abstract String partT();

    /**
     * The second part get this {@link WebHookToken} containing eight (8) alphanumeric characters prefixed with a 'B',
     * formatted as: BXXXXXXXX (where X represents an alphanumeric character).
     *
     * @return the 'B' part
     */
    public abstract String partB();

    /**
     * The last get this {@link WebHookToken} containing twenty-four (24) alphanumeric characters,
     * formatted as: XXXXXXXXXXXXXXXXXXXXXXXX (where X represents an alphanumeric character).
     *
     * @return the last part
     */
    public abstract String partX();

}
