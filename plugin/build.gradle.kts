plugins {
  `java-gradle-plugin`
  `maven-publish`
  kotlin("jvm") version "1.5.31"
}

gradlePlugin {
  plugins {
    create("bigAarPlugin") {
      id = "pink.madis.bigaar"
      implementationClass = "pink.madis.bigaar.BigAarPlugin"
    }
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  compileOnly("com.android.tools.build:gradle:7.0.4")
}
