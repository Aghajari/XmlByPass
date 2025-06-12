plugins {
    alias(libs.plugins.test)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.aghajari.benchmark"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        targetSdk = 35
        lint.targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] =
            "EMULATOR,LOW-BATTERY"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = false
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks.addFirst("release")
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
        }
    }

    targetProjectPath = ":benchmark:content"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}

dependencies {
    implementation(libs.benchmark.macro)
    implementation(libs.benchmark.macro.junit4)
    implementation(libs.runner)
    implementation(libs.ext.junit)
    implementation(libs.junit)
    implementation(libs.benchmark.junit4)

    /*implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.appcompat)

    implementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)*/
}