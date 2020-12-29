
rootProject.name = "instaclone_kt"

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
            maven("https://dl.bintray.com/kotlin/kotlin-eap")
            maven("https://dl.bintray.com/kotlin/kotlin-dev")
        }
    }
}

include("common")
include("client")
include("server")
