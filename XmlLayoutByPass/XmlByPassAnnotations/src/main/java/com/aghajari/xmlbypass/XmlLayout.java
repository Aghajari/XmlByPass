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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface XmlLayout {

    /**
     * Layout file name without .xml extension
     */
    String layout();

    /**
     * Class name of converted layout file
     */
    String className() default "";

    /**
     * Parent of target class,
     * Helps to generate a better layoutParams for root
     */
    String parentClass() default "";

    /**
     * ViewModel is a class that is responsible for preparing and managing the data...
     * Helps you to implement MVVM architecture easy A!
     * Example:
     * <pre>
     *    {@literal <}tag android:id={@literal "@}+id/myText" android:value="live;type=string;Hello World!"/>
     *    {@literal <}tag android:id={@literal "@}+id/myInt" android:value="live;type=int"/>
     * </pre>
     * Will generate
     * <pre>
     *     public class ViewModelClassName extends ViewModel {
     *          MutableLiveData<String> myText = new MutableLiveData<>("Hello World!");
     *          MutableLiveData<Integer> myInt = new MutableLiveData<>();
     *      ...
     * </pre>
     * <p>
     * And you can use live variables just like this:
     * <pre>
     *    {@literal <}TextView
     *        android:id={@literal @"}+id/tv"
     *        android:layout_width="wrap_content"
     *        android:layout_height="wrap_content">
     *        {@literal <}tag android:id="@id/myText" android:value="live;attr=text"/>
     *    {@literal <}/TextView>
     * </pre>
     * The generated code for {@code TextView} will be like :
     * <pre>
     *     viewModel.getMyText().observe(getOwner(), myText -> tv.setText(myText));
     * </pre>
     * <p>
     * Other ways to do this:
     * <pre>
     *     {@literal <}tag android:id="@id/myText" android:value="live;func=setText"/>
     *      OR
     *     {@literal <}tag android:id="@id/myText" android:value="live;src=setText(myText +{@literal &quot;} :) {@literal &quot;})"/>
     * </pre>
     */
    String viewModel() default "";
}
