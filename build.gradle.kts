import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

val appGroupId: String by project
val appName: String by project
val appVersion: String by project
val arrowKtVersion: String by project
val coroutinesVersion: String by project
val gradleWrapperVersion: String by project
val junit5Version: String by project
val jvmTargetVersion: String by project
val kotestVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

group = appGroupId
version = appVersion

plugins {
    kotlin("jvm")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/arrow-kt/arrow-kt/")
}

dependencies {
    implementation("io.arrow-kt:arrow-core-data:$arrowKtVersion")
    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowKtVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutinesVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion") { exclude(group = "io.arrow-kt") }
    testImplementation("org.junit.jupiter:junit-jupiter:$junit5Version")

    testRuntimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    testRuntimeOnly("ch.qos.logback:logback-core:$logbackVersion")
}

configurations {
    all {
        resolutionStrategy.eachDependency {
            when (this.requested.group) {
                "io.arrow-kt" -> this.useVersion(arrowKtVersion)
                "org.jetbrains.kotlin" -> this.useVersion(kotlinVersion)
            }
        }
    }
}

tasks {
    withType<Wrapper> {
        gradleVersion = gradleWrapperVersion
        distributionType = Wrapper.DistributionType.ALL
    }

    compileKotlin {
        kotlinOptions.languageVersion = "1.4"
        kotlinOptions.jvmTarget = jvmTargetVersion
        incremental = true
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=kotlin.Experimental")
    }

    compileTestKotlin {
        kotlinOptions.languageVersion = "1.4"
        kotlinOptions.jvmTarget = jvmTargetVersion
        incremental = true
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=kotlin.Experimental")
    }

    withType<Test> {
        useJUnitPlatform()

        systemProperties = mapOf(
            "junit.jupiter.extensions.autodetection.enabled" to "true",
            "junit.jupiter.execution.parallel.enabled" to "true",
            "junit.jupiter.execution.parallel.mode.default" to "concurrent",
            "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
            "junit.platform.output.capture.stdout" to "true",
            "junit.platform.output.capture.stderr" to "true"
        )

        testLogging {
            events("passed", "skipped", "failed")
            // showExceptions = true
            exceptionFormat = FULL
            // Enable to show output from standard out:
            // showStandardStreams = true
        }
    }
}
