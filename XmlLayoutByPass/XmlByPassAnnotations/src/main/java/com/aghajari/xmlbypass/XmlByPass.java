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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Get the highest UI performance :)
 * <p>
 * XmlByPass is an annotationProcessor library for Android
 * which auto generates the java code of your xml layouts
 * in Source level (before compile)
 *
 * @see <a href="https://github.com/Aghajari/XmlByPass">https://github.com/Aghajari/XmlByPass</a>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface XmlByPass {

    /**
     * List of layouts that XmlByPass must convert to code
     */
    XmlLayout[] layouts();

    /**
     * Path of android-resource layouts directory
     */
    String path() default "";

    /**
     * R.class packageName
     */
    String R() default "";

    /**
     * Layout classes packageName
     */
    String packageName() default "";

    /**
     * True if you want to create an style for unknown attributes,
     * You may need compile twice sometimes
     * to generate R for generated resource file.
     */
    boolean styleable() default false;

    /**
     * True if XmlByPass must convert included layout file (by include tag) too
     * False otherwise
     */
    boolean include() default true;

    /**
     * True if XmlByPass must convert included ViewStub layout (by ViewStub tag) too
     * False otherwise
     */
    boolean viewStub() default true;

    /**
     * Enables or disables ViewModel
     *
     * @see XmlLayout#viewModel()
     */
    boolean viewModel() default true;
}
