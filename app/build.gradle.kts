// File: app/build.gradle.kts

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.studybuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studybuddy"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.21.7")
        eachDependency {
            if (requested.group == "com.google.protobuf") {
                if (requested.name == "protobuf-lite") {
                    useTarget("com.google.protobuf:protobuf-javalite:3.21.7")
                    because("Use protobuf-javalite instead of protobuf-lite to avoid conflicts")
                }
            }
        }
    }
}

dependencies {
    // Firebase (Main App Dependencies)
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore") {
        // Exclude protobuf-lite
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-appcheck")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // Core Android dependencies
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.core:core:1.13.0")
    implementation("androidx.fragment:fragment:1.8.5")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Protobuf Java Lite
    implementation("com.google.protobuf:protobuf-javalite:3.21.7")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("junit:junit:4.12")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.6.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestUtil("androidx.test:orchestrator:1.5.1")

    // Espresso dependencies
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-web:3.6.1")
    androidTestImplementation("androidx.test.espresso.idling:idling-concurrent:3.6.1")
    androidTestImplementation("androidx.test.espresso.idling:idling-net:3.6.1")



    // Firebase Testing (Use Firebase BOM)
    androidTestImplementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    androidTestImplementation("com.google.firebase:firebase-auth")
    androidTestImplementation("com.google.firebase:firebase-firestore") {
        // Exclude protobuf-lite
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    // Protobuf Java Lite for Tests
    androidTestImplementation("com.google.protobuf:protobuf-javalite:3.21.7")

    // Debug Testing
    debugImplementation("androidx.fragment:fragment-testing:1.8.5")
    debugImplementation("androidx.test:core:1.6.1")
}

