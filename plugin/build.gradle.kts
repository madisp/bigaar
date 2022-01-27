plugins {
  `java-gradle-plugin`
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
