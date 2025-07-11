import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.nltv.chafenqi"
    compileSdk = 35

    val versionFile = file("version.properties")

    val versionProperties = Properties()
    if (!versionFile.exists()) {
        versionProperties["VERSION_PATCH"] = 0
        versionProperties["VERSION_MAJOR"] = 0
        versionProperties["VERSION_MINOR"] = 0
        versionProperties["VERSION_BUILD"] = 0
        versionProperties.store(versionFile.writer(), null)
    }

    val mVersionName: String

    val appName = "chafenqi"

    if (versionFile.canRead()) {
        versionProperties.load(FileInputStream(versionFile))
        versionProperties["VERSION_MAJOR"] = (versionProperties.getProperty("VERSION_MAJOR").toInt()).toString()
        versionProperties["VERSION_MINOR"] = (versionProperties.getProperty("VERSION_MINOR").toInt()).toString()
        versionProperties["VERSION_PATCH"] = (versionProperties.getProperty("VERSION_PATCH").toInt()).toString()
        versionProperties["VERSION_BUILD"] = (versionProperties.getProperty("VERSION_BUILD").toInt() + 1).toString()

        val majorVersion = (versionProperties.getProperty("VERSION_MAJOR").toInt()).toString()
        val minorVersion = (versionProperties.getProperty("VERSION_MINOR").toInt()).toString()
        val patchVersion = (versionProperties.getProperty("VERSION_PATCH").toInt()).toString()

        versionProperties.store(versionFile.writer(), null)
        mVersionName = "$majorVersion.$minorVersion.$patchVersion"

        defaultConfig {
            applicationId = "com.nltv.chafenqi"
            minSdk = 28
            targetSdk = 35
            versionCode = versionProperties.getProperty("VERSION_MAJOR").toInt()
            versionName = "$mVersionName (${versionProperties.getProperty("VERSION_BUILD")})"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }

            @Suppress("UnstableApiUsage")
            externalNativeBuild {
                cmake {
                    cppFlags += ""
                }
            }
        }
    } else {
        throw FileNotFoundException("Cannot access version.properties!")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }



    buildTypes {
        debug {
            resValue("string", "username", "testaccount")
            resValue("string", "password", "testtest")

            resValue("string", "serverAddress", "http://43.139.107.206:8083")

            setProperty("archivesBaseName", "$appName-$mVersionName-Build${versionProperties.getProperty("VERSION_BUILD")}")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")

            resValue("string", "username", "")
            resValue("string", "password", "")

            resValue("string", "serverAddress", "http://43.139.107.206:8083")

            setProperty("archivesBaseName", "$appName-$mVersionName-Build${versionProperties.getProperty("VERSION_BUILD")}")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
    }
}

val ktorVersion = "3.0.3"
val composepreferences = "1.0.0"

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(platform("androidx.compose:compose-bom:2025.06.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("com.onesignal:OneSignal:5.1.15")

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("androidx.compose.material:material:1.8.2")
    implementation("io.github.alexzhirkevich:qrose:1.0.1")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.36.0")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.3")

    // core
    implementation("io.github.mflisar.composepreferences:core:$composepreferences")

    implementation("io.github.mflisar.composepreferences:screen-bool:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-button:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-color:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-date:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-input:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-list:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-number:$composepreferences")
    implementation("io.github.mflisar.composepreferences:screen-time:$composepreferences")

    implementation("io.github.mflisar.composepreferences:kotpreferences:$composepreferences")

    implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.3.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:info:1.3.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:input:1.3.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:list:1.3.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:state:1.3.0")

    implementation("me.zhanghai.compose.preference:library:1.1.1")

    implementation("sh.calvin.reorderable:reorderable:2.1.1")

    implementation("dev.shreyaspatil:capturable:2.1.0")
    implementation("me.saket.telephoto:zoomable-image-coil3:0.14.0")
}