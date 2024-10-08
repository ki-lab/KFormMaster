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
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    // Androidx
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.multidex:multidex:2.0.1")

    // Google Places
    implementation("com.google.android.libraries.places:places:2.6.0")

    // KFormMaster
    implementation(project(":form"))
}
