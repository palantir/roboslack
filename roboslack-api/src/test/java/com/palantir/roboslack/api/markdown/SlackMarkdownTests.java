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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.palantir.roboslack.api.testing.MoreReflection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

class SlackMarkdownTests {

    private static final URL EXAMPLE_URL;
    private static final String EXAMPLE_INPUT = "Test";

    static {
        try {
            EXAMPLE_URL = new URL("https://www.palantir.com");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
    }

    private static ThrowingConsumer<ValueDecorator<String>> stringDecoratorConsumer(String input) {
        return decorator -> {
            String expectedDecorated = decorator.prefix().orElse("")
                    + input
                    + decorator.suffix().orElse("");
            assertThat(decorator.decorate(input), is(equalTo(expectedDecorated)));
            assertThat(decorator.decorate(expectedDecorated), is(equalTo(expectedDecorated)));
        };
    }

    private static ThrowingConsumer<TupleDecorator<URL, String>> tupleUrlStringDecoratorConsumer(URL inputUrl,
            String inputText) {
        return decorator -> {
            String expectedDecorated = decorator.prefix().orElse("")
                    + inputUrl.toString()
                    + decorator.separator()
                    + inputText
                    + decorator.suffix().orElse("");
            assertThat(decorator.decorate(inputUrl, inputText), is(equalTo(expectedDecorated)));
        };
    }

    @SuppressWarnings("unchecked")
    private static Stream<TupleDecorator<URL, String>> fieldValuesOfTupleDecoratorUrlString(Class<?> ownerClass) {
        return Arrays.stream(ownerClass.getFields())
                .filter(f -> MoreReflection.fieldIsParameterizedType(f,
                        TupleDecorator.class, URL.class, String.class))
                .map(MoreReflection::getStaticFieldValue)
                .map(object -> (TupleDecorator<URL, String>) object);
    }

    @SuppressWarnings("unchecked")
    private static Stream<ValueDecorator<String>> fieldValuesOfDecoratorString(Class<?> ownerClass) {
        return Arrays.stream(ownerClass.getFields())
                .filter(f -> MoreReflection.fieldIsParameterizedType(f,
                        ValueDecorator.class, String.class))
                .map(MoreReflection::getStaticFieldValue)
                .map(object -> (ValueDecorator<String>) object);
    }

    @TestFactory
    Stream<DynamicTest> testStringDecorators() {
        return DynamicTest.stream(fieldValuesOfDecoratorString(SlackMarkdown.class).iterator(),
                Object::toString,
                stringDecoratorConsumer(EXAMPLE_INPUT));
    }

    @TestFactory
    Stream<DynamicTest> testLinkDecorators() {
        return DynamicTest.stream(fieldValuesOfTupleDecoratorUrlString(SlackMarkdown.class).iterator(),
                Object::toString,
                tupleUrlStringDecoratorConsumer(EXAMPLE_URL, EXAMPLE_INPUT));
    }

}
