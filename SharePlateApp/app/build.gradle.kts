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
        minSdk = 30
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
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Use the latest version
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Keep this consistent with the Credential Manager dependency
    implementation("com.google.android.gms:play-services-identity:18.0.1") // Use the latest version

    // Updated dependencies for Credential Manager:
    implementation("androidx.credentials:credentials:1.3.0-beta01")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0-beta01")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.activity:activity-ktx:1.8.2") // Use activity-ktx for better Kotlin integration
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1") // Use a consistent version with other dependencies
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
}