package pink.madis.bigaar

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

open class BigAarPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.withId("com.android.library") { plugin ->
      configurePlugin(target, plugin as LibraryPlugin)
    }
  }

  private fun configurePlugin(project: Project, plugin: LibraryPlugin) {
    // create the shaded configuration
    val shadedConfig = project.configurations.maybeCreate("shaded")

    shadedConfig.artifacts.forEach {
      println(it)
    }

    // debugImplementation should extend from shaded for normal operation in debug
    project.configurations.getByName("debugImplementation").extendsFrom(shadedConfig)

    // releaseCompileOnly should extend from shaded to make sure javac works
    project.configurations.getByName("releaseCompileOnly").extendsFrom(shadedConfig)

    val extension = project.extensions.getByName("android") as LibraryExtension

    extension.libraryVariants.all {
      if (it.name == "release") {
        val shadeInputsTask = project.tasks.register("shade${it.name.capped}", CreateShadeInputs::class.java) { inputs ->
          inputs.shadedClassesJar.set(project.layout.buildDirectory
            .file("intermediates/bigaar_${it.name}/shaded_classes.jar"))

          inputs.shadeFiles = shadedConfig
        }

        val shadeInputs = project.files(shadeInputsTask).apply {
          builtBy(shadeInputsTask)
        }

        it.registerPostJavacGeneratedBytecode(shadeInputs)
      }
    }
  }
}
