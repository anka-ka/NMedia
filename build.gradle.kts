buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath ("com.android.tools.build:gradle:8.2.2")

    }

}
    plugins {
        id("com.android.application") version "8.5.2" apply false
        id("org.jetbrains.kotlin.android") version "1.5.31" apply false
        id("com.google.gms.google-services") version "4.4.2" apply false
        id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
        id("com.google.dagger.hilt.android") version "2.48" apply false
    }

