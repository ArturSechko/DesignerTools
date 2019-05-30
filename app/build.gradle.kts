plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "com.digitex.designertools"
        minSdkVersion(23)
        targetSdkVersion(28)
        versionCode = 3
        versionName = "2.1.0"
        setProperty("archivesBaseName", "DesignerTools-$versionName")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.31")
    implementation("androidx.core:core-ktx:1.2.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.1.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.fragment:fragment:1.1.0-alpha09")
    implementation("androidx.fragment:fragment-ktx:1.1.0-alpha09")
    implementation("com.google.android.material:material:1.1.0-alpha06")
    implementation("org.cyanogenmod:platform.sdk:6.0")
    implementation("com.larswerkman:lobsterpicker:1.0.1")
    implementation("fr.avianey.com.viewpagerindicator:library:2.4.1.1@aar")
}
