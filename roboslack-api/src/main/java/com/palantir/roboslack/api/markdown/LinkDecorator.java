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

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.palantir.roboslack.utils.MorePreconditions;
import java.net.URL;
import org.immutables.value.Value;

/**
 * The {@link LinkDecorator} produces Slack Markdown hyperlinks as output from a tuple of {@link URL} target and {@link
 * String} text.
 *
 * @see TupleDecorator
 * @see URL
 * @see String
 * @since 0.1.0
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableLinkDecorator.class)
@JsonSerialize(as = ImmutableLinkDecorator.class)
public abstract class LinkDecorator implements TupleDecorator<URL, String> {

    protected static Builder builder() {
        return ImmutableLinkDecorator.builder();
    }

    static LinkDecorator of(String prefix, String separator, String suffix) {
        return builder()
                .prefix(prefix)
                .separator(separator)
                .suffix(suffix)
                .build();
    }

    @Value.Check
    protected final void check() {
        MorePreconditions.checkAtLeastOnePresentAndValid(
                ImmutableList.of("prefix", "suffix"),
                ImmutableList.of(prefix(), suffix()));
        checkArgument(!Strings.isNullOrEmpty(separator()),
                "Separator should be present and valid (non-null, non-empty String)");
    }

    @Override
    public final String decorate(URL url, String text) {
        return StringDecorator.decorate(prefix(), suffix(), Joiner.on(separator()).join(url, text));
    }

    interface Builder {
        Builder prefix(String prefix);
        Builder suffix(String suffix);
        Builder separator(String separator);
        LinkDecorator build();
    }

}
