
plugins {
    id("java")
    kotlin("jvm")
    id("application")
    id("distribution")
}

val ktorVersion = project.property("ktor.version") as String
val logbackVersion = project.property("logback.version") as String
val sqliteVersion = project.property("sqlite.version") as String
val assertJVersion = project.property("assertj.version")

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed:0.3.2")
    implementation("com.h2database:h2:1.4.191")
    implementation("com.mchange:c3p0:0.9.5.2")
    implementation("joda-time:joda-time:2.9.2")
    implementation("org.ehcache:ehcache:3.0.0.m4")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.withType<Copy>().named("processResources") {
    from(project(":client").tasks.named("browserDistribution"))
}