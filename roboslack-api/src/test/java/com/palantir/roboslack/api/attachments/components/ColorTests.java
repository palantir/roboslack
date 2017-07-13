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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.palantir.roboslack.api.testing.MoreAssertions;
import com.palantir.roboslack.api.testing.MoreReflection;
import com.palantir.roboslack.api.testing.ResourcesReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public final class ColorTests {

    private static final String RESOURCES_DIRECTORY = "parameters/attachments/components/colors";

    public static void assertValid(Color color) {
        Assertions.assertFalse(Strings.isNullOrEmpty(color.toString()));
        Assertions.assertFalse(Strings.isNullOrEmpty(color.value()));
        assertThat(color.toString(), is(equalTo(color.value())));
    }

    @ParameterizedTest
    @EnumSource(Color.Preset.class)
    void testPresets(Color.Preset preset) {
        Color color = Color.of(preset);
        assertValid(color);
        assertTrue(color.isPreset());
        assertThat(color.asPreset(), is(equalTo(preset)));
    }

    @ParameterizedTest
    @EnumSource(Color.Preset.class)
    void testPresetStrings(Color.Preset preset) {
        Color color = Color.of(preset.toString());
        assertValid(color);
        assertTrue(color.isPreset());
        assertThat(color.asPreset(), is(equalTo(preset)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "#abcdef",
            "#123456"})
    void testValid(String input) {
        Color color = Color.of(input);
        assertValid(color);
        assertFalse(color.isPreset());
        Throwable thrown = assertThrows(UnsupportedOperationException.class, color::asPreset);
        assertThat(thrown.getMessage(), containsString("not a defined preset"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not-a-hex-color",
            "123456",
            ""})
    void testInvalid(String input) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> Color.of(input));
        assertThat(thrown.getMessage(), containsString("not a valid hex color"));
    }

    @TestFactory
    Stream<DynamicTest> testNoArgStaticFactories() {
        return DynamicTest.stream(
                MoreReflection.findNoArgStaticFactories(Color.class).iterator(),
                Object::toString,
                MoreReflection.noArgStaticFactoryConsumer(ColorTests::assertValid));
    }

    @ParameterizedTest
    @ArgumentsSource(SerializedColorsProvider.class)
    void testSerialization(JsonNode json) {
        MoreAssertions.assertSerializable(json,
                Color.class,
                ColorTests::assertValid);
    }

    static class SerializedColorsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return ResourcesReader.readJson(RESOURCES_DIRECTORY).map(Arguments::of);
        }

    }

}
