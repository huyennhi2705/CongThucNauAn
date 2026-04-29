plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.congthucnauan"
<<<<<<< HEAD
    compileSdk = 34
=======
    compileSdk {
        version = release(36)
    }
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4

    defaultConfig {
        applicationId = "com.example.congthucnauan"
        minSdk = 30
<<<<<<< HEAD
        targetSdk = 34
=======
        targetSdk = 36
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
<<<<<<< HEAD

=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
<<<<<<< HEAD
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Firebase
=======
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
<<<<<<< HEAD
    implementation("com.google.firebase:firebase-storage:20.3.0")
    
    // UI & Navigation
=======
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
<<<<<<< HEAD
    
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
}
=======
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
>>>>>>> 791d8f0549adef46b1e57d3074ae385c6f7f8be4
