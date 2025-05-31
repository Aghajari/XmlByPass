plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    annotationProcessor(libs.auto.service)

    implementation(libs.auto.service)
    implementation(libs.kxml2)
    implementation(project(":XmlByPassAnnotations"))
}