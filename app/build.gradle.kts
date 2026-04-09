import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    }
}

val enableMinifyInReleaseBuilds =
    (findProperty("android.enableMinifyInReleaseBuilds")?.toString()?.toBoolean() ?: true)
val enableShrinkResourcesInReleaseBuilds =
    (findProperty("android.enableShrinkResourcesInReleaseBuilds")?.toString()?.toBoolean() ?: true)

android {
    namespace = "com.moviestreamer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moviestreamer"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // TMDB API key from local.properties or CI secrets.
        // Leave blank to run in public-domain-only mode.
        val tmdbApiKey = project.findProperty("tmdb.api.key")?.toString()?.takeIf { it.isNotBlank() } ?: ""
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = enableMinifyInReleaseBuilds
            isShrinkResources = enableShrinkResourcesInReleaseBuilds
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            keepDebugSymbols += setOf("**/libjlibtorrent-*.so")
        }
    }
}


dependencies {
    // Torrent streaming: FrostWire jlibtorrent (local AAR/JAR dependencies)
    // Place the following files in app/libs/ after downloading/building:
    implementation(files("libs/jlibtorrent-2.0.12.7.jar"))
    implementation(files("libs/jlibtorrent-android-arm-2.0.12.7.jar"))
    implementation(files("libs/jlibtorrent-android-arm64-2.0.12.7.jar"))
    implementation(files("libs/jlibtorrent-android-x86-2.0.12.7.jar"))
    implementation(files("libs/jlibtorrent-android-x86_64-2.0.12.7.jar"))
    // Compose Material Icons (needed for Download, Error, etc.)
    implementation("androidx.compose.material:material-icons-extended")
    // Compose for TV
    // Note: As of Feb 2024, Compose for TV is still in alpha
    // These are the latest stable alpha versions available
    // For production, monitor for beta/stable releases at:
    // https://developer.android.com/jetpack/androidx/releases/tv
    implementation("androidx.tv:tv-foundation:1.0.0-alpha10")
    implementation("androidx.tv:tv-material:1.0.0-alpha10")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // Leanback for TV UI components
    implementation("androidx.leanback:leanback:1.0.0")
    
    // ExoPlayer for video playback
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.0")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Koin for Android dependency injection
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")

    // Room for local storage
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Security – EncryptedSharedPreferences for parental controls PIN
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Background reminders and content notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("androidx.room:room-testing:2.6.1")

    // Android instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("io.mockk:mockk-android:1.13.9")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
}
