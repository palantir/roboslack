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

package com.palantir.roboslack.api.attachments;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.either;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.palantir.roboslack.api.attachments.components.AuthorTests;
import com.palantir.roboslack.api.attachments.components.ColorTests;
import com.palantir.roboslack.api.attachments.components.FieldTests;
import com.palantir.roboslack.api.attachments.components.FooterTests;
import com.palantir.roboslack.api.attachments.components.TitleTests;
import com.palantir.roboslack.api.testing.MoreAssertions;
import com.palantir.roboslack.api.testing.ResourcesReader;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

public final class AttachmentTests {

    private static final String RESOURCES_DIRECTORY = "parameters/attachments";

    public static void assertValid(Attachment attachment) {
        attachment.fields().forEach(FieldTests::assertValid);
        assertFalse(Strings.isNullOrEmpty(attachment.fallback()));
        Optional.ofNullable(attachment.color()).ifPresent(ColorTests::assertValid);
        attachment.pretext().ifPresent(pretext -> assertFalse(Strings.isNullOrEmpty(pretext)));
        Optional.ofNullable(attachment.author()).ifPresent(AuthorTests::assertValid);
        Optional.ofNullable(attachment.title()).ifPresent(TitleTests::assertValid);
        attachment.text().ifPresent(text -> assertFalse(Strings.isNullOrEmpty(text)));
        attachment.imageUrl().ifPresent(imageUrl -> assertFalse(Strings.isNullOrEmpty(imageUrl.toString())));
        attachment.thumbUrl().ifPresent(thumbUrl -> assertFalse(Strings.isNullOrEmpty(thumbUrl.toString())));
        Optional.ofNullable(attachment.footer()).ifPresent(FooterTests::assertValid);
    }

    @SuppressWarnings("unchecked") // Called from reflection
    static Stream<Executable> invalidConstructors() {
        return Stream.of(
                () -> Attachment.builder().fallback("").build(),
                () -> Attachment.builder().fallback("text").pretext("*mark* -down-").build(),
                () -> Attachment.builder().fallback("_markdown_").build()
        );
    }

    @Test
    void testConstructNullFallback() {
        assertThrows(NullPointerException.class, () -> Attachment.builder().fallback(null).build());
    }

    @Test
    void testConstructNoState() {
        assertThrows(IllegalStateException.class, () -> Attachment.builder().build());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidConstructors")
    void testConstructionConstraints(Executable executable) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, executable);
        assertThat(thrown.getMessage(), either(containsString("cannot be null or empty"))
                .or(containsString("cannot contain markdown")));
    }

    @ParameterizedTest
    @ArgumentsSource(SerializedAttachmentsProvider.class)
    void testDeserialization(JsonNode json) {
        MoreAssertions.assertSerializable(json,
                Attachment.class,
                AttachmentTests::assertValid);
    }

    static class SerializedAttachmentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return ResourcesReader.readJson(RESOURCES_DIRECTORY).map(Arguments::of);
        }

    }

}
