pluginManagement {
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "com.vanniktech.maven.publish") {
        useModule("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
      }
    }
  }
  repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    google()
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
  }
}

include(":plugin")
