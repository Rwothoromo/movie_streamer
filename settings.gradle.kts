pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // Use FAIL_ON_PROJECT_REPOS for reproducible builds
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://dl.frostwire.com/maven")
            content {
                includeGroup("com.frostwire")
            }
        }
    }
}

rootProject.name = "MovieStreamer"
include(":app")
