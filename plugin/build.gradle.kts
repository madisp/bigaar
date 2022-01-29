import com.vanniktech.maven.publish.SonatypeHost

plugins {
  `java-gradle-plugin`
  `maven-publish`
  kotlin("jvm") version "1.5.31"
  id("com.vanniktech.maven.publish")
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
  implementation("org.ow2.asm:asm:9.2")
  implementation("org.ow2.asm:asm-commons:9.2")
}

mavenPublish {
  sonatypeHost = SonatypeHost.DEFAULT
}
