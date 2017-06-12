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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.utils.MorePreconditions;
import com.palantir.roboslack.utils.MoreStrings;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.immutables.value.Value;

/**
 * The {@link StringDecorator} produces Slack Markdown formatted {@link String} objects.
 *
 * @see ValueDecorator
 * @see String
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableStringDecorator.class)
@JsonSerialize(as = ImmutableStringDecorator.class)
public abstract class StringDecorator implements ValueDecorator<String> {

    static StringDecorator of(String prefix, String suffix) {
        return builder().prefix(prefix).suffix(suffix).build();
    }

    static StringDecorator of(String prefixAndSuffix) {
        return of(prefixAndSuffix, prefixAndSuffix);
    }

    static StringDecorator ofPrefix(String prefix) {
        return builder().prefix(prefix).build();
    }

    static StringDecorator ofSuffix(String suffix) {
        return builder().suffix(suffix).build();
    }

    static Builder builder() {
        return ImmutableStringDecorator.builder();
    }

    @Value.Check
    protected final void check() {
        MorePreconditions.checkAtLeastOnePresentAndValid(
                ImmutableList.of("prefix", "suffix"),
                ImmutableList.of(prefix(), suffix()));
    }

    static String decorate(Optional<String> prefix, Optional<String> suffix, String line) {
        String prefixed = prefix.map(p -> MoreStrings.safePrepend(line, p)).orElse(line);
        return suffix.map(s -> MoreStrings.safeAppend(prefixed, s)).orElse(prefixed);
    }

    @Override
    public final String decorate(String input) {
        return decorate(prefix(), suffix(), input);
    }

    @Override
    public final String decorateMultiline(Iterable<? extends String> inputs) {
        return Joiner.on(SlackMarkdown.NEWLINE_SEPARATOR)
                .join(StreamSupport.stream(inputs.spliterator(), true)
                        .map(this::decorate)
                        .collect(ImmutableList.toImmutableList()));
    }

    interface Builder {
        Builder prefix(String prefix);
        Builder suffix(String suffix);
        StringDecorator build();
    }

}
