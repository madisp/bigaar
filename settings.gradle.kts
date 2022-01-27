pluginManagement {
  repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    google()
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
  }
}

include(":plugin")
