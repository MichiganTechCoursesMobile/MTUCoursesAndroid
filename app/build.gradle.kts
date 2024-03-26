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
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
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
  val nav_version = "2.8.0-alpha05"

  implementation("androidx.navigation:navigation-compose:$nav_version")

  implementation("androidx.room:room-runtime:$room_version")
  annotationProcessor("androidx.room:room-compiler:$room_version")

  // To use Kotlin Symbol Processing (KSP)
  ksp("androidx.room:room-compiler:$room_version")

  // optional - Kotlin Extensions and Coroutines support for Room
  implementation("androidx.room:room-ktx:$room_version")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
  implementation("androidx.compose.ui:ui:1.7.0-alpha05")
  implementation("androidx.compose.material3:material3:1.3.0-alpha03")
  implementation("androidx.activity:activity:1.9.0-beta01")
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation("androidx.compose.material:material-icons-extended")
  implementation(platform(libs.androidx.compose.bom))

  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.retrofit)
  implementation("com.squareup.retrofit2:converter-gson:2.5.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

}