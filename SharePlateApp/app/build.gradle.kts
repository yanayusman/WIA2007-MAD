plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.shareplateapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shareplateapp"
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
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Firebase BoM - Use this to manage Firebase dependency versions
    implementation(platform("com.google.firebase:firebase-bom:32.7.1")) // Use the latest version

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx") // Use the Kotlin extensions
    implementation("com.google.firebase:firebase-analytics-ktx") // Use the Kotlin extensions

    // Google Play Services - Auth and Identity
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Match version with Credential Manager
    implementation("com.google.android.gms:play-services-identity:18.0.1")

    // Credential Manager
    implementation("androidx.credentials:credentials:1.2.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")

    // Other Core Dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation("androidx.activity:activity-ktx:1.8.2") // Kotlin extensions for Activity

    // Google Identity Services
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Other Libraries
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    // Google Play Services Tasks (ensure latest)
    implementation("com.google.android.gms:play-services-tasks:18.0.2")

    // Firebase Auth and Google Play Services
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}