plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.vanniktech.maven.publish") version "0.36.0"
    id("signing")
}

group = "io.github.gourav1908"
version = "1.0.0-alpha04"

val hasSigningKey = providers.gradleProperty("signingInMemoryKey")
    .orNull
    .isNullOrBlank()
    .not()
val hasSigningPassword = providers.gradleProperty("signingInMemoryKeyPassword")
    .orNull
    .isNullOrBlank()
    .not()
val isMavenCentralPublish = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("MavenCentral", ignoreCase = true)
}

android {
    namespace = "io.github.permissionflow"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

mavenPublishing {
    publishToMavenCentral()

    if (isMavenCentralPublish || (hasSigningKey && hasSigningPassword)) {
        signAllPublications()
    }

    coordinates(
        groupId = "io.github.gourav1908",
        artifactId = "permission-flow",
        version = "1.0.0-alpha04"
    )

    pom {

        name.set("Permission Flow")

        description.set(
            "Modern Compose-first Android permission manager SDK"
        )

        url.set(
            "https://github.com/gourav1908/permission-flow"
        )

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("gourav1908")
                name.set("Gourav Bhatnagar")
                url.set("https://github.com/gourav1908")
            }
        }

        scm {
            url.set("https://github.com/gourav1908/permission-flow")
            connection.set("scm:git:git://github.com/gourav1908/permission-flow.git")
            developerConnection.set("scm:git:ssh://git@github.com/gourav1908/permission-flow.git")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
