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

/**
 * This interface extends the {@link Decorator} interface by enforcing implementing classes to apply Slack
 * markdown formatting to single value {@link T}s from plain {@link T}s.
 *
 * @param <T> input type that is decorated by this interface.
 * @see Decorator
 * @since 0.1.0
 */
public interface ValueDecorator<T> extends Decorator<T> {

    /**
     * Applies the {@link Decorator#prefix()} and {@link Decorator#suffix()} to the provided {@code input}.
     *
     * @param input the input to apply the decoration too
     * @return the resulting decorated output
     */
    T decorate(T input);

    /**
     * Applies the {@link Decorator#prefix()} and {@link Decorator#suffix()} to the {@link Iterable} of {@code inputs},
     * using the implementors definition of handling multiline objects (ie. using {@code \n} newline separator for
     * {@link String}s).
     *
     * @param inputs the inputs to individually apply decoration too
     * @return the resulting single combined decorated object
     */
    T decorateMultiline(Iterable<? extends T> inputs);

}
