plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aghajari.test"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aghajari.test"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<JavaCompile> {
    val resDir = "${projectDir}/src/main/res"
    options.compilerArgs.add("-AxmlByPassResDir=$resDir")
    inputs.dir(resDir)
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    annotationProcessor(project(":XmlByPass"))
    implementation(project(":XmlByPassAnnotations"))
}