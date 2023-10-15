plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "api.luisangeldd.mediapicker"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        version = "1.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.compose.material3:material3-android:1.2.0-alpha09")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.compose.ui:ui-util:1.5.3")
    // Icons
    implementation ("androidx.compose.material:material-icons-extended:1.5.3")
    // coil
    implementation("io.coil-kt:coil-compose:2.4.0")
}
group = "com.github.luisangeldd"
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.luisangeldd"
                artifactId = "MediaPicker"
                version = "1.2.0"
            }
        }
    }
}