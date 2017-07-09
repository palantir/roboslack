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

package com.palantir.roboslack.api.attachments.components;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.base.Strings;
import com.palantir.roboslack.api.testing.ResourcesDeserializer;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;

public final class FieldTests {

    public static void assertValid(Field field) {
        assertFalse(Strings.isNullOrEmpty(field.title()));
        assertFalse(Strings.isNullOrEmpty(field.value()));
    }

    @SuppressWarnings("unused") // Called from reflection
    static Stream<Executable> invalidMarkdownConstructors() {
        return Stream.of(
                () -> Field.of("*title with bold*", "Valid"),
                () -> Field.builder().title("_Sad Times_")
                        .value("Hello *failing* test! :smile:").build()
        );
    }

    @ParameterizedTest
    @ArgumentsSource(SerializedFieldsProvider.class)
    void testDeserialization(Field field) {
        assertValid(field);
    }

    @ParameterizedTest
    @MethodSource(names = "invalidMarkdownConstructors")
    void testTitleCannotContainMarkdown(Executable executable) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, executable);
        assertThat(thrown.getMessage(), containsString("cannot contain markdown"));
    }

    static class SerializedFieldsProvider implements ArgumentsProvider {

        private static final String RESOURCES_DIRECTORY = "parameters/attachments/components/fields";

        @Override
        public Stream<? extends Arguments> arguments(ContainerExtensionContext context) throws Exception {
            return ResourcesDeserializer.deserialize(Field.class, RESOURCES_DIRECTORY)
                    .map(ObjectArrayArguments::create);
        }
    }

}
