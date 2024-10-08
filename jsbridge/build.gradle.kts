plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.takwolf.android.jsbridge"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    compileOnly("androidx.annotation:annotation:1.8.1")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.takwolf.android.jsbridge"
            artifactId = "jsbridge"
            version = "0.0.3"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
