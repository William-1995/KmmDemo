import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    jacoco
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.kable)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

//        tasks.register("jacocoTestReport", JacocoReport::class) {
//            dependsOn(tasks.withType(Test::class))
//            val coverageSourceDirs = arrayOf(
//                "src/commonMain",
//            )
//
//            val buildDirectory = layout.buildDirectory
//
//            val classFiles = buildDirectory.dir("classes/kotlin/jvm").get().asFile
//                .walkBottomUp()
//                .toSet()
//
//            classDirectories.setFrom(classFiles)
//            sourceDirectories.setFrom(files(coverageSourceDirs))
//
//            buildDirectory.files("jacoco/jvmTest.exec").let {
//                executionData.setFrom(it)
//            }

//
//            reports {
//                xml.required = true
//                csv.required = true
//                html.required = true
//                println(html.outputLocation.get().asFile.path)
//            }
//        }
    }
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

jacoco {
    toolVersion="0.8.12"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

tasks.forEach {
    it -> println(it.name)
}

println(tasks.findByName("build"))


tasks.withType(Test::class) {
    println("aaaa")
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.STANDARD_OUT)
    }
    finalizedBy(tasks.withType(JacocoReport::class))
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn(tasks.findByName("build"))

    val coverageSourceDirs = arrayOf(
        "src/commonMain"
    )
//
//    val buildDir = layout.buildDirectory

// Include all compiled classes.
//    val classFiles = buildDir.dir("*").get().asFile
//        .walkBottomUp()
//        .toSet()
//    println(buildDir.dir("*/kotlin/*").get().asFile.path)
// This helps with test coverage accuracy.
//    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

// The resulting test report in binary format.
// It serves as the basis for human-readable reports.
//    buildDir.files("*.exec").let {
//        println(it)
//        println("===")
//
//        executionData.setFrom(it)
//    }

    reports {
        xml.required = true
        csv.required = true
        html.required = true
        println(html.outputLocation.get().asFile.path)
    }
}
