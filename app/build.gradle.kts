plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id ("com.google.devtools.ksp")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.sedat.note"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sedat.note"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.sedat.note.HiltTestRunner" //sonradan eklendi.

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
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
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test.espresso:espresso-contrib:3.5.1")

    // TestImplementations
    testImplementation("junit:junit:4.13.2")
    implementation ("androidx.test:core:1.5.0")
    testImplementation ("org.hamcrest:hamcrest-all:1.3")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.robolectric:robolectric:4.8.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation ("com.google.truth:truth:1.1.3")
    testImplementation ("org.mockito:mockito-core:4.7.0")
    testImplementation("androidx.room:room-testing:2.6.0")

    // Android Test Implementations
    androidTestImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("org.mockito:mockito-android:4.7.0")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation ("com.google.truth:truth:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("org.mockito:mockito-core:4.7.0")
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.43.2")
    //kspAndroidTest ("com.google.dagger:hilt-android-compiler:2.48")
    debugImplementation ("androidx.fragment:fragment-testing:1.7.0-alpha06")

    //dependency injection
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    //ksp ("androidx.hilt:hilt-compiler:1.0.0")

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.6.0")

    //room
    implementation ("androidx.room:room-runtime:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")
    implementation ("com.google.code.gson:gson:2.10.1")

    //coil
    implementation("io.coil-kt:coil:2.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    //camera
    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("com.google.guava:guava:31.0.1-android")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
}
