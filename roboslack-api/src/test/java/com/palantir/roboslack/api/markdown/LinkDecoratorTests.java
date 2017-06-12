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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class LinkDecoratorTests {

    @SuppressWarnings("unused") // Called via reflection
    static Stream<Executable> illegalStateConstructors() {
        return Stream.of(
                () -> LinkDecorator.builder().suffix("").build(),
                () -> LinkDecorator.builder().prefix("").build()
        );
    }

    @SuppressWarnings("unused") // Called via reflection
    static Stream<Executable> illegalArgumentConstructors() {
        return Stream.of(
                () -> LinkDecorator.of("", "", ""),
                () -> LinkDecorator.of("[", "", ""),
                () -> LinkDecorator.of("", "|", "")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "illegalStateConstructors")
    void testIllegalStateConstructors(Executable executable) {
        assertThrows(IllegalStateException.class, executable);
    }

    @ParameterizedTest
    @MethodSource(names = "illegalArgumentConstructors")
    void testIllegalArgumentConstruction(Executable executable) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, executable);
        assertThat(thrown.getMessage(), containsString("present and valid"));
    }

}
