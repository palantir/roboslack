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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.palantir.roboslack.utils.MorePreconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.CheckForNull;
import org.immutables.value.Value;

/**
 * Class that allows color-coding message attachments to quickly relay intent. A color can be one of 'good' (green),
 * 'warning' (yellow), 'danger' (red), or a color hex text, including the initial # symbol.
 *
 * @see <a href="https://api.slack.com/docs/message-attachments">Slack Message Attachments</a>
 * @since 0.1.0
 */
@Value.Immutable
@Value.Style(jacksonIntegration = false)
@JsonDeserialize(using = Color.Deserializer.class)
public abstract class Color {

    private static Builder builder() {
        return ImmutableColor.builder();
    }

    public static Color of(Preset preset) {
        return builder().value(preset.toString()).build();
    }

    @JsonCreator
    public static Color of(String value) {
        return builder().value(value).build();
    }

    public static Color good() {
        return of(Preset.GOOD);
    }

    public static Color warning() {
        return of(Preset.WARNING);
    }

    public static Color danger() {
        return of(Preset.DANGER);
    }

    @Value.Check
    final Color normalize() {
        return value().toLowerCase().equals(value())
                ? this
                : of(value().toLowerCase());
    }

    @Value.Check
    protected final void check() {
        if (!Preset.of(value()).isPresent()) {
            MorePreconditions.checkHexColor(value());
        }
    }

    /**
     * True if this {@link Color} is a defined {@link Preset}, false otherwise.
     *
     * @return whether or not this color is a preset
     */
    @Value.Derived
    public boolean isPreset() {
        Preset.DANGER.value();
        return Preset.of(value()).isPresent();
    }

    /**
     * Gets the representation get this {@link Color} as its associated {@link Preset}.  If this {@link Color} is not
     * a {@link Preset}, this method will throw a {@link UnsupportedOperationException}, so it is safer to first check
     * if this {@link Color#isPreset()}.
     *
     * @return this color represented as a {@link Preset}
     */
    public Preset asPreset() {
        if (!Preset.of(value()).isPresent()) {
            throw new UnsupportedOperationException(
                    String.format("The color value get '%s' is not a defined preset, valid preset values include: [%s]",
                            value(),
                            Joiner.on(",").join(Preset.values())));
        }
        return Preset.of(value()).get();
    }

    @Override
    public final String toString() {
        return value();
    }

    @JsonIgnoreType
    public enum Preset {
        /**
         * Green.
         */
        GOOD("good"),
        /**
         * Yellow.
         */
        WARNING("warning"),
        /**
         * Red.
         */
        DANGER("danger");

        private final String value;

        Preset(String value) {
            this.value = value;
        }

        public static Optional<Preset> of(@CheckForNull String value) {
            return Arrays.stream(Preset.values())
                    .filter(preset -> preset.toString().equalsIgnoreCase(value))
                    .findFirst();
        }

        @Override
        public String toString() {
            return value;
        }

        public String value() {
            return value;
        }
    }

    protected interface Builder {
        Builder value(String value);
        Color build();
    }

    static class Deserializer extends JsonDeserializer<Color> {

        @Override
        public Color deserialize(JsonParser parser, DeserializationContext ctxt)
                throws IOException {
            return Color.of(parser.getValueAsString());
        }
    }

    /**
     * Represents either a hex color value in the form of {@code #XXXXXX} or any defined {@link Preset#toString()}.
     *
     * @return the color value
     */
    @JsonValue
    public abstract String value();

}
