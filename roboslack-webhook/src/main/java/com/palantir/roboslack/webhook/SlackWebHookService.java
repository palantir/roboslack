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

package com.palantir.roboslack.webhook;

import static com.google.common.base.Preconditions.checkNotNull;

import com.palantir.remoting2.retrofit2.Retrofit2Client;
import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.webhook.api.SlackWebHook;
import com.palantir.roboslack.webhook.api.model.WebHookToken;
import com.palantir.roboslack.webhook.api.model.response.ResponseCode;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Main entry point class to interact with a {@link SlackWebHook}. Instantiate it with a {@link WebHookToken} and a
 * {@code userAgent} {@link String}, then send your composed {@link MessageRequest}s via the {@link
 * SlackWebHookService#sendMessage(MessageRequest)} method. Ensure that you check the returned {@link ResponseCode} for
 * Slack status feedback.
 */
public final class SlackWebHookService {

    private static final String DEFAULT_USER_AGENT = "RoboSlack/1.0";
    private static final String DEFAULT_WEB_HOOK_URL = "https://hooks.slack.com/services/";

    private final WebHookToken token;
    private final SlackWebHook webHook;

    private SlackWebHookService(WebHookToken token, String userAgent) {
        this.token = checkNotNull(token, "WebHookToken must be valid and non-null.");
        this.webHook = Retrofit2Client.builder()
                .build(SlackWebHook.class, userAgent, DEFAULT_WEB_HOOK_URL);
    }

    /**
     * Creates a new instance of the {@link SlackWebHookService} using the provided {@link WebHookToken}.
     *
     * @param token the {@link WebHookToken} to use for connecting to the {@link SlackWebHook}
     * @return the new {@link SlackWebHookService} interaction object
     */
    public static SlackWebHookService with(WebHookToken token) {
        return new SlackWebHookService(token, DEFAULT_USER_AGENT);
    }

    /**
     * Creates a new instance of the {@link SlackWebHookService} using the provided {@link WebHookToken} and connecting
     * {@code usageAgent} {@link String}.
     *
     * @param token the {@link WebHookToken} to use for connecting to the {@link SlackWebHook}
     * @param userAgent the {@code userAgent} {@link String} to use when connecting to the {@link SlackWebHook}
     * @return the new {@link SlackWebHookService} interaction object
     */
    public static SlackWebHookService with(WebHookToken token, String userAgent) {
        return new SlackWebHookService(token, userAgent);
    }

    /**
     * We can't serialize the response correctly since Slack only sends back a 'text/html' string,
     * so we manually pull it from the {@link ResponseBody} instead.
     */
    private static String executeCallAndGetResponseBody(Call<ResponseBody> call) {
        try {
            ResponseBody body = call.execute().body();
            if (body != null) {
                return body.string();
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException("Unable to execute call", e);
        }
    }

    /**
     * Sends a message to connected {@link SlackWebHookService}.
     *
     * @param messageRequest the {@link MessageRequest} to execute sending
     * @return the resulting {@link ResponseCode} from the operation
     */
    public ResponseCode sendMessage(MessageRequest messageRequest) {
        return ResponseCode.of(executeCallAndGetResponseBody(webHook
                .sendMessage(token.partT(), token.partB(), token.partX(), messageRequest)));
    }

}
