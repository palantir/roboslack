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

package com.palantir.roboslack.webhook.api;

import com.palantir.roboslack.api.MessageRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for services that wish to function as a Slack Incoming Webhook.
 * Implementing classes must be able to accept a {@code MessageRequest} along with Slack T, B, and X token parts.
 * @see <a href="https://api.slack.com/incoming-webhooks">Slack documentation</a> for more information.
 * @since v0.0.2
 */
@Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
@Consumes(MediaType.APPLICATION_JSON)
public interface SlackWebHook {

    @POST("{token_t_part}/{token_b_part}/{token_x_part}")
    Call<ResponseBody> sendMessage(
            @Path("token_t_part") String tokenTPart,
            @Path("token_b_part") String tokenBPart,
            @Path("token_x_part") String tokenXPart,
            @Body MessageRequest messageRequest);

}
