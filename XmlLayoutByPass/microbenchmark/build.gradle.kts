plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.benchmark)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.aghajari.microbenchmark"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        targetSdk = 35

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }

    testBuildType = "release"
    buildTypes {
        debug {
            // Since isDebuggable can"t be modified by gradle for library modules,
            // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-proguard-rules.pro"
            )
        }
        release {
            isDefault = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.benchmark.junit4)
    // Add your dependencies here. Note that you cannot benchmark code
    // in an app module this way - you will need to move any code you
    // want to benchmark to a library module:
    // https://developer.android.com/studio/projects/android-library#Convert

    implementation(libs.appcompat)
    kaptAndroidTest(project(":XmlByPass"))
    androidTestImplementation(project(":XmlByPassAnnotations"))
}