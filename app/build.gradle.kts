import jdk.jfr.internal.JVM.exclude

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.underworld"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.underworld"
        minSdk = 25
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    // fire base fcm token for notifications
    //noinspection GradleDependency,UseTomlInstead
    implementation(platform("com.google.firebase:firebase-bom:35.0.2"))
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics")
    //noinspection UseTomlInstead
    implementation ("com.google.firebase:firebase-messaging")


    // retrofit for api android
    //noinspection UseTomlInstead,NewerVersionAvailable
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //noinspection NewerVersionAvailable,UseTomlInstead
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


   // Google Sign-In SDK
    //noinspection UseTomlInstead,GradleDependency
    implementation("com.google.android.gms:play-services-auth:20.7.0")


    // glide image set
    //noinspection UseTomlInstead,NewerVersionAvailable
    implementation("com.github.bumptech.glide:glide:4.16.0")



    implementation("io.socket:socket.io-client:2.1.0") {
        exclude(group = "org.json", module = "json")
    }


}