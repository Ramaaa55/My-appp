plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.mysticmango.idealtattooia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mysticmango.idealtattooia"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    // FIX: Keep only one material3 declaration
    implementation("androidx.compose.material3:material3:1.2.1")

    // FIX: Remove duplicate compose UI declarations
    // implementation("androidx.compose.ui:ui:1.6.7")  // ← Remove this line

    // FIX: Use BOM versions for other compose components
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // FIX: Remove duplicates and keep single BOM reference
    implementation("androidx.activity:activity-compose:1.9.0")

    // FIX: Remove duplicate material3 declarations
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // FIX: Keep only one coil version
    implementation("io.coil-kt:coil-compose:2.6.0")

    // FIX: Remove these duplicates
    // implementation("androidx.compose.ui:ui:1.6.7")
    // implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Core
    implementation("androidx.core:core-ktx:1.13.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(libs.coil.compose)
    implementation(libs.androidx.appcompat)

    // Versiones explícitas sin BOM
    implementation("androidx.compose.foundation:foundation-layout:1.6.7") // Para weight
    implementation("androidx.compose.material3:material3:1.2.1")

    // Usar versión compatible con SDK 34
    implementation("androidx.recyclerview:recyclerview:1.3.2")  // ← Versión segura

    // Añadir estas dependencias EXACTAS
    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.compose.material3:material3:1.2.1") // Versión estable
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Accompanist (version matches your Compose 1.6.7)
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Update Coil to use Accompanist integration
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Remove duplicate material3 declarations
    implementation("androidx.compose.material3:material3:1.2.1") // Keep only one
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
} 