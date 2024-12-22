// Add necessary imports
import java.util.Properties
import java.io.FileInputStream

/**
 * Project build configuration file.
 * Defines the project structure, dependencies, and build settings.
 */
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ridwan.tales_of_dd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ridwan.tales_of_dd"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load Google Maps API key from local.properties
        val localProperties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }
        val MAPS_API_KEY: String = localProperties.getProperty("MAPS_API_KEY", "")
        manifestPlaceholders["MAPS_API_KEY"] = MAPS_API_KEY
    }

    buildTypes {
        // Release build configuration
        release {
            isMinifyEnabled = false  // Disable code minification
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java compatibility settings
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android core dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI Components
    implementation("de.hdodenhof:circleimageview:3.1.0")  // Circular ImageView
    implementation("com.github.bumptech.glide:glide:4.16.0")  // Image loading library
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.android.material:material:1.11.0")  // Material Design

    // Google Maps Services
    implementation("com.google.android.gms:play-services-maps:18.2.0")  // Maps SDK
    implementation("com.google.android.gms:play-services-location:21.0.1")  // Location Services

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
}