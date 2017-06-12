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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.markdown.SlackMarkdown;
import java.util.List;
import org.junit.jupiter.api.Test;

class MessageRequestTests {

    private static final String INPUT_EMOJI = "smile";

    private static MessageRequest defaultWithIconEmoji(String iconEmoji) {
        return MessageRequest.builder()
                .username("robo-slack")
                .iconEmoji(iconEmoji)
                .text("The simplest message")
                .build();
    }

    private static Attachment defaultAttachment(int index) {
        return Attachment.builder()
                .fallback(String.format("Fallback %d", index))
                .text(String.format("Text %d", index))
                .build();
    }

    private static List<Attachment> attachments(int count) {
        ImmutableList.Builder<Attachment> attachments = ImmutableList.builder();
        for (int i = 0; i < count; i++) {
            attachments.add(defaultAttachment(i));
        }
        return attachments.build();
    }

    @Test
    void testNormalizationEmoji() {
        MessageRequest message = defaultWithIconEmoji(INPUT_EMOJI);
        MessageRequest slackMarkdown = defaultWithIconEmoji(SlackMarkdown.EMOJI.decorate(INPUT_EMOJI));
        assertThat(message, is(equalTo(slackMarkdown)));
    }

    @Test
    void testTooManyAttachments() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () ->
                MessageRequest.builder()
                        .username("robo-slack")
                        .text("Too many attachments")
                        .attachments(attachments(MessageRequest.MAX_ATTACHMENTS_COUNT + 1))
                        .build());
        assertThat(thrown.getMessage(), containsString(String.format("Cannot exceed %s attachments for one message",
                MessageRequest.MAX_ATTACHMENTS_COUNT)));
    }

}
