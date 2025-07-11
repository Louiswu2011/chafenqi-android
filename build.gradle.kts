buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.29" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("com.osacky.doctor") version "0.10.0"
}

doctor {
    javaHome {
        ensureJavaHomeIsSet = false
        ensureJavaHomeMatches = false
        failOnError = false
    }
}