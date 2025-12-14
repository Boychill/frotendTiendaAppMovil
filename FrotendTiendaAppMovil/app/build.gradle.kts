plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.api.frotendtiendaappmovil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.api.frotendtiendaappmovil"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // 🔐 CONFIGURACIÓN DE FIRMA (APK RELEASE)
    signingConfigs {
        create("release") {
            storeFile = file("keystore/app-release-key.jks")
            storePassword = "clansupply"
            keyAlias = "releaseKey"
            keyPassword = "clansupply"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    // --- CORE & COMPOSE ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // --- Tema del Splash ---
    implementation("androidx.core:core-splashscreen:1.0.1")
    // --- ICONOS ---
    implementation("androidx.compose.material:material-icons-extended")

    // --- NAVEGACIÓN ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- HILT (Inyección de Dependencias) ---
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")

    // --- RED (Retrofit & OkHttp) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- IMÁGENES (Coil) ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- DATASTORE ---
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // --- LOCATION & TASKS ---
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ==========================================
    //              TESTING (UNITARIOS)
    // ==========================================
    testImplementation("junit:junit:4.13.2")

    // Corrutinas para Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // MockK (Reemplaza a Mockito)
    testImplementation("io.mockk:mockk:1.13.8")

    // Turbine (Para probar Flows/StateFlows)
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // ==========================================
    //           TESTING UI (ANDROID TEST)
    // ==========================================
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // MockK para Android (Instrumentado)
    androidTestImplementation("io.mockk:mockk-android:1.13.8")

    // Hilt para Tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51")

    // Herramientas de depuración UI
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
