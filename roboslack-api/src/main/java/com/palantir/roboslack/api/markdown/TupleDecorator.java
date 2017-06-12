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
 * This interface extends the {@link Decorator} interface by enforcing implementing classes to apply composite markdown
 * formatting using two different input types {@link S} and {@link T}, resulting in Slack markdown.
 *
 * @param <S> first required type for this {@link Decorator} to produce Slack Markdown
 * @param <T> second required type for this {@link Decorator} to produce Slack Markdown
 * @see Decorator
 * @since 0.1.0
 */
public interface TupleDecorator<S, T> extends Decorator<T> {

    /**
     * Applies the {@link Decorator#prefix()} and {@link Decorator#suffix()} decoration around the joined {@code key}
     * and {@code value} using the {@link TupleDecorator#separator()}.
     *
     * @param key the first input to decorate
     * @param value the second input to decorate
     * @return the decorated value
     */
    T decorate(S key, T value);

    /**
     * The {@link String} {@code separator} used for joining the two input types decorated from the {@link
     * TupleDecorator#decorate(Object, Object)} call.
     *
     * @return the {@link String} decoration separator
     */
    String separator();

}
