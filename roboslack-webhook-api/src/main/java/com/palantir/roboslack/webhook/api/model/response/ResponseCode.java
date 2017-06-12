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

package com.palantir.roboslack.webhook.api.model.response;

/**
 * Response Codes to expect from {@link com.palantir.roboslack.webhook.api.SlackWebHook} calls.
 *
 * @see <a href="https://api.slack.com/methods/chat.postMessage#errors">https://api.slack.com/methods/
 * chat.postMessage#errors</a> for a full list and descriptions.
 * @since 0.1.0
 */
public enum ResponseCode {
    OK,
    CHANNEL_NOT_FOUND,
    NOT_IN_CHANNEL,
    IS_ARCHIVED,
    MSG_TOO_LONG,
    NO_TEXT,
    TOO_MANY_ATTACHMENTS,
    RATE_LIMITED,
    NOT_AUTHED,
    INVALID_AUTH,
    ACCOUNT_INACTIVE,
    INVALID_ARG_NAME,
    INVALID_ARRAY_ARG,
    INVALID_CHARSET,
    INVALID_FORM_DATA,
    INVALID_POST_TYPE,
    MISSING_POST_TYPE,
    REQUEST_TIMEOUT,
    MISSING_CHARSET,
    SUPERFLUOUS_CHARSET;

    public static ResponseCode of(String string) {
        for (ResponseCode code : ResponseCode.values()) {
            if (code.toString().equalsIgnoreCase(string)) {
                return code;
            }
        }
        throw new IllegalArgumentException(String.format("No ResponseCode found matching value: %s", string));
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
