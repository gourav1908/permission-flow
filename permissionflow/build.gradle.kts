import java.util.Base64

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

signing {
    val fileSigningKey = providers.gradleProperty("signing.keyFile")
        .orElse(providers.environmentVariable("SIGNING_KEY_FILE"))
        .orNull
        ?.let { keyFile ->
            file(keyFile).readText()
        }
    val plainSigningKey = providers.gradleProperty("signing.key")
        .orElse(providers.environmentVariable("SIGNING_KEY"))
        .orNull
    val base64SigningKey = providers.environmentVariable("SIGNING_KEY_BASE64")
        .orNull
    val signingKey = (fileSigningKey ?: base64SigningKey?.let { encodedKey ->
            String(
                Base64.getDecoder().decode(encodedKey)
            )
        } ?: plainSigningKey)
        ?.replace("\\n", "\n")
    val signingPassword = providers.gradleProperty("signing.password")
        .orElse(providers.environmentVariable("SIGNING_PASSWORD"))
        .orNull

    if (!signingKey.isNullOrBlank() &&
        !signingPassword.isNullOrBlank()
    ) {
        useInMemoryPgpKeys(
            signingKey,
            signingPassword
        )
        sign(publishing.publications)
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

afterEvaluate {

    publishing {

        publications {

            create<MavenPublication>("release") {

                from(components["release"])

                groupId = "io.github.gourav1908"

                artifactId = "permission-flow"

                version = "1.0.0-alpha01"

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

                            name.set("Apache-2.0")

                            url.set(
                                "https://opensource.org/licenses/Apache-2.0"
                            )
                        }
                    }

                    developers {

                        developer {

                            id.set("gourav1908")

                            name.set("Gourav Bhatnagar")
                        }
                    }

                    scm {

                        connection.set(
                            "scm:git:git://github.com/gourav1908/permission-flow.git"
                        )

                        developerConnection.set(
                            "scm:git:ssh://github.com/gourav1908/permission-flow.git"
                        )

                        url.set(
                            "https://github.com/gourav1908/permission-flow"
                        )
                    }
                }
            }
        }

        repositories {

            maven {

                name = "MavenCentral"

                url = uri(
                    "https://central.sonatype.com/api/v1/publisher/upload"
                )

                credentials {

                    username =
                        providers.gradleProperty(
                            "mavenCentralUsername"
                        ).orElse(
                            providers.environmentVariable(
                                "MAVEN_CENTRAL_USERNAME"
                            )
                        ).getOrElse("")

                    password =
                        providers.gradleProperty(
                            "mavenCentralPassword"
                        ).orElse(
                            providers.environmentVariable(
                                "MAVEN_CENTRAL_PASSWORD"
                            )
                        ).getOrElse("")
                }
            }
        }
    }
}
