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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.attachments.components.Author;
import com.palantir.roboslack.api.attachments.components.Color;
import com.palantir.roboslack.api.attachments.components.Field;
import com.palantir.roboslack.api.attachments.components.Footer;
import com.palantir.roboslack.api.attachments.components.Title;
import com.palantir.roboslack.api.markdown.SlackMarkdown;
import com.palantir.roboslack.webhook.api.model.WebHookToken;
import com.palantir.roboslack.webhook.api.model.response.ResponseCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SlackWebHookServiceTests {

    private static WebHookToken assumingEnvironmentWebHookToken() {
        try {
            return WebHookToken.fromEnvironment();
        } catch (IllegalArgumentException iae) {
            assumeTrue(false, iae::getLocalizedMessage);
        }
        throw new IllegalStateException("Unable to run assumption and skip test, this should never happen");
    }

    @ParameterizedTest
    @ArgumentsSource(MessageRequestProvider.class)
    void testSendMessage(MessageRequest messageRequest, TestInfo testInfo) {
        assertThat(SlackWebHookService.with(assumingEnvironmentWebHookToken())
                        .sendMessage(EnrichTestMessageRequest.get().apply(messageRequest, testInfo)),
                is(equalTo(ResponseCode.OK)));
    }

    @ParameterizedTest
    @ArgumentsSource(MessageRequestProvider.class)
    void testSendMessageAsync(MessageRequest messageRequest, TestInfo testInfo) {
        AtomicBoolean submitted = new AtomicBoolean(false);
        SlackWebHookService.with(assumingEnvironmentWebHookToken())
                .sendMessage(EnrichTestMessageRequest.get().apply(messageRequest, testInfo),
                        new Callback<ResponseCode>() {
                            @Override
                            @ParametersAreNonnullByDefault
                            public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                                submitted.set(true);
                                assertTrue(call.isExecuted());
                                assertThat(response.body(), is(equalTo(ResponseCode.OK)));
                            }

                            @Override
                            @ParametersAreNonnullByDefault
                            public void onFailure(Call<ResponseCode> call, Throwable throwable) {
                                submitted.set(true);
                                assertTrue(call.isExecuted());
                                fail(throwable);
                            }
                        });
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilTrue(submitted);
    }

    static class MessageRequestProvider implements ArgumentsProvider {

        private static final MessageRequest MESSAGE_SIMPLE = MessageRequest.builder()
                .username("robo-slack")
                .iconEmoji(SlackMarkdown.EMOJI.decorate("smile"))
                .text("The simplest message")
                .build();

        private static final MessageRequest MESSAGE_WITH_ATTACHMENT_FOOTER = MessageRequest.builder()
                .username("robo-slack")
                .iconEmoji("smile")
                .text("A message with a footer")
                .addAttachments(Attachment.builder()
                        .fallback("Fallback text")
                        .text("This should have a footer")
                        .footer(Footer.builder()
                                .text("The small text")
                                .timestamp(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault()).toEpochSecond())
                                .build())
                        .color(Color.good())
                        .build())
                .build();

        private static final MessageRequest MESSAGE_WITH_ATTACHMENTS = MessageRequest.builder()
                .username("robo-slack")
                .iconEmoji(SlackMarkdown.EMOJI.decorate("smile"))
                .text(String.format("Now with an attachment %s - %s",
                        SlackMarkdown.MENTION_CHANNEL.decorate("slack-dev-msgs"),
                        SlackMarkdown.MENTION_USER.decorate("username")))
                .addAttachments(
                        Attachment.builder()
                                .fallback("Attachment fallback")
                                .color(Color.of("#C0FFEE"))
                                .text("An attachment")
                                .build(),
                        Attachment.builder()
                                .fallback("Fallback")
                                .color(Color.danger())
                                .text("Another attachment")
                                .build())
                .build();

        private static final MessageRequest MESSAGE_COMPLEX = MessageRequest.builder()
                .username("robo-slack")
                .iconEmoji(SlackMarkdown.EMOJI.decorate("train"))
                .text(String.format("USE ALL THE FIELDS! And a list:%s%s",
                        SlackMarkdown.NEWLINE_SEPARATOR,
                        SlackMarkdown.LIST_SINGLE_LEVEL.decorateMultiline(
                                ImmutableList.of("List Item 1", "List Item 2"))))
                .addAttachments(Attachment.builder()
                        .fallback("Fallback text...")
                        .color(Color.warning())
                        .pretext("Some pretext")
                        .author(Author.builder()
                                .icon(url("https://platform.slack-edge.com/img/default_application_icon.png"))
                                .link(url("https://platform.slack-edge.com/img/default_application_icon.png"))
                                .name("Some Author")
                                .build())
                        .title(Title.builder()
                                .text("A title")
                                .link(url("http://www.palantir.com"))
                                .build())
                        .addFields(Field.builder()
                                .isShort(true)
                                .title("Field 1 Title")
                                .value("Field 1 Text")
                                .build())
                        .addFields(Field.of("Field 2 Title", "Field 2 Text"))
                        .imageUrl(url("https://www.palantir.com/videos/gotham_workflow.jpg"))
                        .thumbUrl(url("https://www.palantir.com/build/images/about/phil-eng-hero.jpg"))
                        .footer(Footer.builder()
                                .text("Footer Text")
                                .icon(url("https://platform.slack-edge.com/img/default_application_icon.png"))
                                .timestamp(LocalDateTime.now()
                                        .atZone(ZoneId.systemDefault()).toEpochSecond())
                                .build())
                        .build())
                .build();

        private static URL url(String url) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e.getLocalizedMessage(), e);
            }
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    MESSAGE_SIMPLE,
                    MESSAGE_WITH_ATTACHMENT_FOOTER,
                    MESSAGE_WITH_ATTACHMENTS,
                    MESSAGE_COMPLEX
            ).map(Arguments::of);
        }
    }

}
