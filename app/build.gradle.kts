plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    id("kotlin-kapt")
}

subprojects {
    plugins.withType<com.android.build.gradle.BasePlugin>().configureEach {
        project.plugins.apply("org.jetbrains.kotlin.android")
    }
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
        debug {
            isDebuggable = true
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
    }

    aaptOptions {
        additionalParameters.add("--warn-manifest-validation")
    }

    lint {
        checkReleaseBuilds = true
        abortOnError = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.7"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.13.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // Material
    implementation("com.google.android.material:material:1.11.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    // Añadir dependencias de Retrofit y Moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    // Añadir estas dependencias
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // AdMob
    implementation("com.google.android.gms:play-services-ads:22.6.0")
}

// Configuración de resolución fuera del bloque dependencies
configurations.all {
    resolutionStrategy {
        force(
            "com.squareup.okhttp3:okhttp:4.12.0",
            "com.squareup.okhttp3:logging-interceptor:4.12.0"
        )
    }
}