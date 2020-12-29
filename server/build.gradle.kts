plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("application")
    id("distribution")
}

val ktorVersion = project.property("ktor.version") as String
val logbackVersion = project.property("logback.version") as String
val kotlinxSerializationVersion = project.property("kotlinx.serialization.version") as String

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("com.mchange:c3p0:0.9.5.5")
    implementation("org.ehcache:ehcache:3.0.0.m4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    implementation("com.h2database", "h2", "1.4.200")
    implementation("org.jetbrains.exposed", "exposed-jodatime", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")
}

application {
//    mainClassName = "server.ServerKt"
    mainClassName = "io.ktor.server.netty.EngineMain"
    applicationDefaultJvmArgs = listOf("--host", "localhost", "--port", "62813")
    executableDir = rootProject.path
}

tasks.withType<Copy>().named("processResources") {
    from(project(":client").tasks.named("browserDistribution"))
}
