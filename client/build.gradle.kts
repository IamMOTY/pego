plugins {
    kotlin("js")
}
group = "me.romart"
version = "1.0-SNAPSHOT"

val kotlinxSerializationVersion = project.property("kotlinx.serialization.version") as String // 1.0.0

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}

dependencies {
    val kotlinVersion = "1.4.20"
    val muirwikComponentVersion = "0.6.3"
    val kotlinJsVersion = "pre.129-kotlin-$kotlinVersion"
    implementation(project(":common"))
    testImplementation(kotlin("test-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
    implementation(kotlin("stdlib-js", kotlinVersion))
    implementation("org.jetbrains", "kotlin-styled", "5.2.0-$kotlinJsVersion")
    implementation(npm("react-hot-loader", "^4.12.20"))
    implementation("com.ccfraser.muirwik:muirwik-components:$muirwikComponentVersion")
}

kotlin {
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        useCommonJs()
    }
}