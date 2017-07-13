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

package com.palantir.roboslack.api.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.jackson.ObjectMappers;
import java.io.IOException;
import java.util.function.Consumer;

public final class MoreAssertions {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMappers.newObjectMapper();

    private MoreAssertions() {}

    public static <T> void assertSerializable(JsonNode serialized, Class<T> clazz, Consumer<T> assertion) {
        try {
            // First try deserializing
            T instance = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(serialized), clazz);
            assertion.accept(instance);
            // Then reserializing and comparing
            String reserialized = OBJECT_MAPPER.writeValueAsString(instance);
            assertEquals(serialized.toString(), reserialized,
                    String.format("Serialized input %s does not match reserialized string: %s",
                            Joiner.on(serialized.toString()).join(ImmutableList.of(System.lineSeparator())),
                            Joiner.on(reserialized).join(ImmutableList.of(System.lineSeparator()))));
        } catch (IOException e) {
            fail(e);
        }
    }

}
