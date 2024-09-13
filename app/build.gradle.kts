plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsKotlinAndroid)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.mtucoursesmobile.michigantechcourses"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.mtucoursesmobile.michigantechcourses"
    minSdk = 29
    targetSdk = 34
    versionCode = 2
    versionName = "alpha-1.0b"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  val room_version = "2.6.1"
  val nav_version = "2.7.7"

  implementation("com.kizitonwose.calendar:compose:2.6.0-beta02")

  implementation(libs.androidx.navigation.compose)

  implementation(libs.androidx.room.runtime)
  annotationProcessor(libs.room.compiler)

  // To use Kotlin Symbol Processing (KSP)
  ksp(libs.room.compiler)

  // optional - Kotlin Extensions and Coroutines support for Room
  implementation(libs.androidx.room.ktx)
  implementation(libs.kotlinx.coroutines.android)
  implementation("androidx.compose.material3:material3:1.3.0-beta04")
  implementation(libs.compose.shimmer)
  implementation(libs.androidx.activity)
  implementation(libs.ui)
  implementation(libs.coil.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.material.icons.extended)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

}