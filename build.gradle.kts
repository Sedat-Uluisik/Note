// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}*/
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.6.0" apply false
    id ("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}