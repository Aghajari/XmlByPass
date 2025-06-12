/*
 * Copyright (C) 2022 - Amir Hossein Aghajari
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
 *
 */

package com.aghajari.xmlbypass.attributes;

import com.aghajari.xmlbypass.SourceGenerator;

import java.util.HashMap;

public interface AttributesParser {

    /**
     * @return supported attrs by this parser
     */
    String[] getKeys();

    /**
     * @return true if this parser can parse some attrs of this map
     */
    boolean supports(HashMap<String, String> map);

    default boolean supports(HashMap<String, String> map, String name) {
        return supports(map);
    }

    /**
     * Parses supported attrs
     * And removes them from map
     */
    String[] parse(HashMap<String, String> map);

    default String[] parse(HashMap<String, String> map, String viewName, SourceGenerator generator) {
        return parse(map);
    }

    default void resetTag() {
    }

    /**
     * @return the classes which parser needs to import
     */
    default String[] imports() {
        return null;
    }

    /**
     * @return true if you want to use {@link #parse(HashMap, String, SourceGenerator)},
     * false if you want to simply use {@link #parse(HashMap)}.
     * You can also add extra codes to source here
     */
    default boolean needsToAddIdAtFirst(SourceGenerator generator) {
        return true;
    }

    /**
     * @return true if SourceGenerator should add semicolon
     * at the end of each line of parser output
     */
    default boolean needsSemicolon() {
        return true;
    }

}
