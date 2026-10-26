buildscript {
    repositories { google() }
    dependencies {
        // Newer R8 than AGP 8.9 bundles, so release minification understands Kotlin 2.2 metadata.
        classpath(libs.android.r8)
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}