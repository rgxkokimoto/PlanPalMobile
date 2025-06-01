plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.planpalmobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.planpalmobile"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.material.v1110)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.annotation)
    implementation(libs.activity)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    // Libreria para el calendario
    implementation("com.applandeo:material-calendar-view:1.9.2")
    // Para gestionar fechas
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4") // (Temporal hasta API)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Dependencias necesarias para el uso de retro feet
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Necesario para leer con el gson en la api
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.activity:activity-ktx:1.8.0") // Para ActivityResult API

   // implementation ("com.google.firebase:firebase-analytics")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Para las fotos
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // para las etiquetas de las horas
    implementation("com.google.android.flexbox:flexbox:3.0.0")



    // build.gradle (nivel app)
   /* implementation ("com.google.firebase:firebase-messaging:23.4.0")
    implementation ("com.google.firebase:firebase-functions:20.3.1")
*/



}