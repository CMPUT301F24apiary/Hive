plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hive"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hive"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.espresso.intents)
    implementation(libs.ext.junit)

    // Unit testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testImplementation(libs.testng)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.0.0")

    // App dependencies
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.storage)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.glide.v4110)
    annotationProcessor(libs.compiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.13.0")
    androidTestImplementation(libs.testng) // Glide for image handling
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    // Firebase authentication
    implementation(libs.play.services.auth)
    implementation(libs.play.services.base)

    // Glide (for profile images)
    implementation(libs.glide)

    // JavaDoc
//    implementation(files("C:/Users/Zach/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))
    implementation(files("/home/vidhipatel/Android/Sdk/platforms/android-34/android.jar"))
}
