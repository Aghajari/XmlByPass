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

package com.aghajari.xmlbypass;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add an external attribute parser to XmlByPass
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Repeatable(value = XmlByPassAttrs.class)
@Target(ElementType.TYPE)
public @interface XmlByPassAttr {

    /**
     * full name of attribute, for example:
     * <code>android:text</code>
     * <code>app:srcCompat</code>
     */
    String name();

    /**
     * format of attribute:
     *
     * <code>reference</code>
     * <code>string</code>
     * <code>boolean</code>
     * <code>integer</code>
     * <code>float</code>
     * <code>dimension</code>
     * <code>dimension|float</code>
     * <code>color</code>
     * <code>drawable</code>
     * <code>${type}_id</code>
     */
    String format();

    /**
     * the property name of the attribute
     */
    String codeName() default "";

    /**
     * True if property needs context as first parameter
     */
    boolean context() default false;

    /**
     * True if you want to ignore this attribute
     */
    boolean ignoreAttr() default false;

    /**
     * Enums list for this attribute
     */
    XmlByPassAttrEnum[] enums() default {};
}
