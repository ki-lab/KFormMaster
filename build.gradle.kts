buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath("io.deepmedia.tools:publisher:0.7.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
