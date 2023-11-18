import com.android.build.api.dsl.Packaging
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream
import java.io.FileNotFoundException

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.nltv.chafenqi"
    compileSdk = 34

    val versionFile = file("version.properties")
    var value = 0
    val versionProperties = Properties()
    if (!versionFile.exists()) {
        versionProperties["VERSION_PATCH"] = 0
        versionProperties["VERSION_NUMBER"] = 0
        versionProperties["VERSION_BUILD"] = 0
        versionProperties.store(versionFile.writer(), null)
    }

    val runningTasks = gradle.startParameter.taskNames
    if ("assembleRelease" in runningTasks) {
        value = 1
    }

    var mVersionName: String

    val appName = "chafenqi"
    val majorVersion = "1"
    val minorVersion = "0"

    if (versionFile.canRead()) {
        versionProperties.load(FileInputStream(versionFile))
        versionProperties["VERSION_PATCH"] = (versionProperties.getProperty("VERSION_PATCH").toInt() + value).toString()
        versionProperties["VERSION_NUMBER"] = (versionProperties.getProperty("VERSION_NUMBER").toInt() + value).toString()
        versionProperties["VERSION_BUILD"] = (versionProperties.getProperty("VERSION_BUILD").toInt() + 1).toString()

        versionProperties.store(versionFile.writer(), null)
        mVersionName = "$majorVersion.$minorVersion.${versionProperties.getProperty("VERSION_PATCH")}"

        defaultConfig {
            applicationId = "com.nltv.chafenqi"
            minSdk = 28
            targetSdk = 33
            versionCode = versionProperties.getProperty("VERSION_NUMBER").toInt()
            versionName = "$mVersionName (${versionProperties.getProperty("VERSION_BUILD")})"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
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
        buildConfig = true
    }



    buildTypes {
        debug {
            resValue("string", "username", "testaccount")
            resValue("string", "password", "testtest")

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


            setProperty("archivesBaseName", "$appName-$mVersionName-Build${versionProperties.getProperty("VERSION_BUILD")}")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
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

task("bumpServerBuildVersion") {

}

val ktorVersion = "2.3.6"

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0-beta01")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("com.github.tehras:charts:0.2.4-alpha")
    implementation("io.github.alexzhirkevich:qrose:1.0.0-beta02")
    implementation("com.github.MFlisar:ComposePreferences:0.3")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
}