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


import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.palantir.roboslack.api.testing.MoreAssertions;
import com.palantir.roboslack.api.testing.ResourcesReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public final class FieldTests {

    private static final String RESOURCES_DIRECTORY = "parameters/attachments/components/fields";

    public static void assertValid(Field field) {
        assertFalse(Strings.isNullOrEmpty(field.title()));
        assertFalse(Strings.isNullOrEmpty(field.value()));
    }

    @ParameterizedTest
    @ArgumentsSource(SerializedFieldsProvider.class)
    void testSerialization(JsonNode json) {
        MoreAssertions.assertSerializable(json,
                Field.class,
                FieldTests::assertValid);
    }

    static class SerializedFieldsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return ResourcesReader.readJson(RESOURCES_DIRECTORY).map(Arguments::of);
        }

    }

}
