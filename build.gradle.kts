// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0")
    }
}*/
plugins {
    id("com.android.application") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.7.3" apply false
    id ("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}