plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.vishalbothe.smart_event_sdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.coroutines)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2ConverterGson)
    implementation(libs.retrofitMock)


    testImplementation(libs.junit)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.coroutineTest)
    testImplementation(libs.jupiter)
    testImplementation(libs.jupiterEnginer)


    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation("io.mockk:mockk-android:1.13.7")
    testImplementation(kotlin("test"))

}