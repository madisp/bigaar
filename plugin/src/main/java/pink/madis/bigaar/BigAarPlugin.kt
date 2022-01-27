package pink.madis.bigaar

import org.gradle.api.Plugin
import org.gradle.api.Project

open class BigAarPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.logger.info("Hello world from big-aar-plugin")
  }
}
