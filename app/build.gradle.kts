repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.thejuki.kformmasterexample"

    compileSdk = 34
    defaultConfig {
        applicationId = namespace
        minSdk = 21
        targetSdk = 34
        multiDexEnabled = true
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    // Androidx
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.multidex:multidex:2.0.1")

    // Google Places
    implementation("com.google.android.libraries.places:places:3.5.0")

    // KFormMaster
    implementation(project(":form"))
}
