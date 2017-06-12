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

package com.palantir.roboslack.api.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StringDecoratorTests {

    private static final String EXAMPLE_INPUT_STRING = "Test Input";

    @SuppressWarnings("unused") // Called via reflection
    static Stream<Executable> nullPointerExceptionConstructors() {
        return Stream.of(
                () -> StringDecorator.builder().prefix(null).build(),
                () -> StringDecorator.builder().suffix(null).build(),
                () -> StringDecorator.builder().prefix(null).suffix(null).build(),
                () -> StringDecorator.ofPrefix(null),
                () -> StringDecorator.ofSuffix(null),
                () -> StringDecorator.of(null)
        );
    }

    @SuppressWarnings("unused") // Called via reflection
    static Stream<Executable> illegalArgumentConstructors() {
        return Stream.of(
                () -> StringDecorator.builder().prefix("").build(),
                () -> StringDecorator.builder().suffix("").build(),
                () -> StringDecorator.ofPrefix(""),
                () -> StringDecorator.ofSuffix(""),
                () -> StringDecorator.of("")
        );
    }

    @SuppressWarnings("unused")
    static Stream<StringDecorator> validDecorators() {
        return Stream.of(
                StringDecorator.of("x"),
                StringDecorator.ofPrefix("y"),
                StringDecorator.ofSuffix("z")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "nullPointerExceptionConstructors")
    void testNullConstruction(Executable executable) {
        assertThrows(NullPointerException.class, executable);
    }

    @ParameterizedTest
    @MethodSource(names = "illegalArgumentConstructors")
    void testInvalidConstruction(Executable executable) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, executable);
        assertThat(thrown.getMessage(), containsString("present and valid"));
    }

    @ParameterizedTest
    @MethodSource(names = "validDecorators")
    void testDecorate(StringDecorator decorator) {
        String decorated = decorator.decorate(EXAMPLE_INPUT_STRING);
        assertThat(decorated, is(not(equalTo(EXAMPLE_INPUT_STRING))));
        decorator.prefix().ifPresent(prefix ->
                assertTrue(decorated.startsWith(prefix)));
        decorator.suffix().ifPresent(suffix ->
                assertTrue(decorated.endsWith(suffix)));
    }

}
