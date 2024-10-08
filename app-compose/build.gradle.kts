import java.time.Instant
import java.time.ZoneId

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeArg)
    alias(libs.plugins.appVersioning)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    kotlin("kapt")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

appVersioning {
    overrideVersionCode { _, _, _ ->
        Instant.now().atZone(ZoneId.of("UTC+00:00")).toEpochSecond().toInt()
    }

    overrideVersionName { gitTag, _, _ ->
        "1.0-${gitTag.commitHash}"
    }
}

android {
    namespace = "com.dexcare.acme.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dexcare.acme.android"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["auth0Domain"] = ""
        manifestPlaceholders["auth0Scheme"] = "https"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.graphics)
    implementation(libs.androidx.compose.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.appcompat)
    implementation(libs.material)
    debugImplementation(libs.androidx.compose.tooling)

    // Using the Auth0 Lock widget for simplicity.  This could be replaced by a custom UI.
    implementation(libs.auth0)
    implementation(libs.timber)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.compiler)

    coreLibraryDesugaring(libs.desugaring.jdk)

    implementation(libs.dexcare)
    implementation(libs.stripe)

    implementation(libs.kotlin.serialization)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.junit)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.test.compose.ui)
    debugImplementation(libs.test.compose.manifest)

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
