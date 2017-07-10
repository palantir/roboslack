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

import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.clients.SlackClients;
import com.palantir.roboslack.webhook.api.SlackWebHook;
import com.palantir.roboslack.webhook.api.model.WebHookToken;
import com.palantir.roboslack.webhook.api.model.response.ResponseCode;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Main entry point class to interact with a {@link SlackWebHook}. Instantiate it with a {@link WebHookToken} and a
 * {@code userAgent} {@link String}, then send your composed {@link MessageRequest}s via the {@link
 * SlackWebHookService#sendMessageAsync(MessageRequest)} method. Ensure that you check the returned {@link ResponseCode}
 * for Slack status feedback.
 */
public final class SlackWebHookService {

    private static final String TOKEN_ERR = "WebHookToken must be valid and non-null.";

    private static final String DEFAULT_WEB_HOOK_URL = "https://hooks.slack.com/services/";

    private final WebHookToken token;
    private final SlackWebHook webHook;

    private SlackWebHookService(WebHookToken token, String userAgent) {
        this.token = checkNotNull(token, TOKEN_ERR);
        this.webHook = SlackClients.create(SlackWebHook.class, userAgent, DEFAULT_WEB_HOOK_URL,
                ResponseCodeConverter.factory());
    }

    private SlackWebHookService(WebHookToken token) {
        this.token = checkNotNull(token, TOKEN_ERR);
        this.webHook = SlackClients.create(SlackWebHook.class, DEFAULT_WEB_HOOK_URL,
                ResponseCodeConverter.factory());
    }

    /**
     * Creates a new instance of the {@link SlackWebHookService} using the provided {@link WebHookToken}.
     *
     * @param token the {@link WebHookToken} to use for connecting to the {@link SlackWebHook}
     * @return the new {@link SlackWebHookService} interaction object
     */
    public static SlackWebHookService with(WebHookToken token) {
        return new SlackWebHookService(token);
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

    private Call<ResponseCode> sendCall(MessageRequest messageRequest) {
        return webHook.sendMessage(token.partT(), token.partB(), token.partX(), messageRequest);
    }

    /**
     * Sends a message to a connected {@link SlackWebHookService} asynchronously using provided {@link Callback}.
     *
     * @param messageRequest the {@link MessageRequest} to execute sending
     * @param callback the {@link Callback} to trigger on response
     */
    public void sendMessageAsync(MessageRequest messageRequest, Callback<ResponseCode> callback) {
        sendCall(messageRequest).enqueue(callback);
    }

    /**
     * Sends a message to connected {@link SlackWebHookService} synchronously.
     *
     * @param messageRequest the {@link MessageRequest} to execute sending
     * @return the resulting {@link ResponseCode} from the operation
     * @throws IllegalStateException if unable to connect to Slack
     */
    public ResponseCode sendMessageAsync(MessageRequest messageRequest) {
        try {
            return sendCall(messageRequest).execute().body();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not connect to %s.", DEFAULT_WEB_HOOK_URL), e);
        }
    }

}
