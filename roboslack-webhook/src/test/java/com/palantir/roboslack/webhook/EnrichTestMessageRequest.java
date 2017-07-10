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

import static com.google.common.base.Preconditions.checkArgument;

import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.api.attachments.Attachment;
import java.util.function.BiFunction;
import org.junit.jupiter.api.TestInfo;

final class EnrichTestMessageRequest implements BiFunction<MessageRequest, TestInfo, MessageRequest> {

    private EnrichTestMessageRequest() {}

    public static EnrichTestMessageRequest get() {
        return new EnrichTestMessageRequest();
    }

    @Override
    public MessageRequest apply(MessageRequest messageRequest, TestInfo testInfo) {
        checkArgument(testInfo.getTestClass().isPresent());
        checkArgument(testInfo.getTestMethod().isPresent());
        String methodName = testInfo.getTestMethod().get().getName();
        String className = testInfo.getTestClass().get().getSimpleName();
        String basicSummary = String.format("Called from %s within %s",
                methodName,
                className);
        return MessageRequest.builder()
                .from(messageRequest)
                .addAttachments(Attachment.builder()
                        .fallback(basicSummary)
                        .text(basicSummary)
                        .build())
                .build();
    }

}
