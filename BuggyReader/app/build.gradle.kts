plugins {
    // AGP 9 already has Kotlin built in, so org.jetbrains.kotlin.android is not needed anymore
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.vlu.buggyreader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vlu.buggyreader"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
