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


import java.util.Optional;

/**
 * This interface represents a Slack Markdown {@link Decorator} type in RoboSlack that can have a prefix and suffix.
 *
 * @param <T> input type that is decorated by this interface
 * @since 0.1.0
 */
public interface Decorator<T> {

    Optional<T> prefix();

    Optional<T> suffix();

}
