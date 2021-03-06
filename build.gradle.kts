import org.jetbrains.kotlin.daemon.client.launchProcessWithFallback
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.5.0"
val serializationVersion = "1.2.0"
val ktorVersion = "1.5.4"

plugins {
    kotlin("multiplatform") version "1.5.0"
    application //to run JVM part
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.wjp"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/") // react, styled, ...
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {

            }
            commonWebpackConfig {

            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                add(commonMain)
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("org.litote.kmongo:kmongo-coroutine:4.2.7")            }
        }

        val jsMain by getting {
            dependencies {
                add(commonMain)
                implementation("io.ktor:ktor-client-core:$ktorVersion")

                implementation("io.ktor:ktor-client-js:$ktorVersion") //include http&websockets

                //ktor client js json
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")

                val versionRect = "17.0.1-pre.148-kotlin-1.4.21"
                val versionRectNpm = "17.0.2"
                implementation("org.jetbrains:kotlin-react:$versionRect")
                implementation("org.jetbrains:kotlin-react-dom:$versionRect")
                implementation(npm("react", versionRectNpm))
                implementation(npm("react-dom", versionRectNpm))


            }
        }
    }
}

application {
    mainClassName = "com.wjp.server.ServerKt"
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.name == "") {
            useVersion("123")
        }
    }

}