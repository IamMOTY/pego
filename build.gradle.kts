import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    kotlin("multiplatform") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
    id("org.hidetake.ssh") version "2.10.1"
}

allprojects {
    version = "0.1.1"


    repositories {
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

tasks.register<Copy>("stageServer") {
    dependsOn("server:build")

    destinationDir = File("build/dist/server")

    from(tarTree("server/build/distributions/server-0.1.1.tar"))
}

tasks.register<Copy>("stageClient") {
    dependsOn("client:browserDistribution")

    destinationDir = File("build/dist/client")

    from("client/build/distributions")
}

tasks.register("stage") {
    dependsOn("stageServer")
    dependsOn("stageClient")
}

//task("awsDeploy") {
//    dependsOn("stage")
//
//    val awsProperties = java.util.Properties()
//    awsProperties.load(project.rootProject.file("local.properties").inputStream())
//
//    val passPhrase = System.getenv("PASSPHRASE") ?: ""
//    val awsUser = awsProperties.getProperty("aws.hostUser") as String
//    val awsHost = awsProperties.getProperty("aws.host") as String
//    val port = awsProperties.getProperty("aws.port") as String
//    val dbHost = awsProperties.getProperty("aws.dbHost") as String
//    val dbUser = awsProperties.getProperty("aws.dbUser") as String
//    val dbPassword = awsProperties.getProperty("aws.dbPassword") as String
//
//    val awsRemote = remotes.create("awsRemote") {
//        host = awsHost
//        user = awsUser
//        passphrase = passPhrase
//        identity = File("${System.getProperty("user.home")}/.ssh/id_rsa")
//    }
//
//    doLast {
//        ssh.run(delegateClosureOf<org.hidetake.groovy.ssh.core.RunHandler> {
//            session(awsRemote, delegateClosureOf<org.hidetake.groovy.ssh.session.SessionHandler> {
//                logger.lifecycle("Killing existing jvms")
//                execute("sudo killall java || true")
//
//                logger.lifecycle("Create a directory")
//
//                execute("rm -rf ~/app || true")
//                execute("mkdir -p ~/app || true")
//
//                val distDir = File(rootDir, "build/dist")
//
//                logger.lifecycle("Copy bundle to $awsHost")
//
//                put(
//                    hashMapOf(
//                        "from" to distDir.absolutePath,
//                        "into" to "/home/$awsUser/app",
//                        "fileTransfer" to "scp"
//                    )
//                )
//
//                logger.lifecycle("Run server...")
//
//                execute("cd /home/$awsUser/app/dist; nohup sudo bash server/server-0.1.1/:/server --host \"$awsHost\" --port \"$port\" --dbHost \"$dbHost\" --dbUser \"$dbUser\" --dbPassword \"$dbPassword\" > /dev/null 2>&1 &")
//            })
//        })
//    }
//}