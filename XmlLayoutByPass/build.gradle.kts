// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.benchmark) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.test) apply false
    kotlin("kapt") version "2.1.21" apply false
}