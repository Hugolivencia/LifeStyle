import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")



}

android {
    namespace = "com.example.lifestyle"
    compileSdk = 34


    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.lifestyle"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding{
        enable = true
    }

}

dependencies {

    // Glide ( añdir fotos )
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // FireBase
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation (platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation ("com.google.android.gms:play-services-auth:21.1.0")
    implementation ("com.google.firebase:firebase-database-ktx:20.3.1")
    implementation ("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    // Librería para añadir animaciones
    implementation("com.airbnb.android:lottie:3.4.2")


    // Google-services
    implementation("com.google.gms:google-services:4.4.1")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.1")
    // Dependencias animación
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navegacion entre componentes
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0")

    // Swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")


    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.android.support:support-annotations:28.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
