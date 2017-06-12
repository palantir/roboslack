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

package com.palantir.roboslack.utils;


/**
 * Contains several convenience methods for interacting with and checking {@link String}s.
 *
 * @since 0.1.0
 */
public final class MoreStrings {

    private MoreStrings() {
        // Intentionally left blank
    }

    /**
     * Convenience method to prepend a {@link String} with another {@link String}, if it doesn't start with that
     * {@link String} already.
     *
     * @param text {@link String} text to check
     * @param toPrepend {@link String} to check and prepend
     * @return {@link String} with other {@link String} prepended
     */
    public static String safePrepend(String text, String toPrepend) {
        return !text.startsWith(toPrepend)
                ? toPrepend + text
                : text;
    }

    /**
     * Convenience method to append a {@link String} with another {@link String}, if it doesn't end with that
     * {@link String} already.
     *
     * @param text {@link String} text to check
     * @param toAppend {@link String} to check and append
     * @return {@link String} with other {@link String} appended
     */
    public static String safeAppend(String text, String toAppend) {
        return !text.endsWith(toAppend)
                ? text + toAppend
                : text;
    }

}
