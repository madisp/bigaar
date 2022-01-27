package pink.madis.bigaar

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

@Suppress("unused")
open class BigAarPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.withId("com.android.library") {
      val extension = target.extensions.create("bigaar", BigAarExtension::class.java)
      configurePlugin(extension, target)
    }
  }

  @Suppress("UnstableApiUsage")
  private fun configurePlugin(extension: BigAarExtension, project: Project) {
    // create the shaded configuration
    val shadedConfig = project.configurations.maybeCreate("shaded")

    // debugImplementation should extend from shaded for normal operation in debug
    project.configurations.getByName("debugImplementation").extendsFrom(shadedConfig)

    // releaseCompileOnly should extend from shaded to make sure javac works
    project.configurations.getByName("releaseCompileOnly").extendsFrom(shadedConfig)

    val legacyExt = project.extensions.getByName("android") as LibraryExtension

    val mappingFiles = mutableMapOf<String, Provider<RegularFile>>()

    legacyExt.libraryVariants.all {
      if (it.name == "release") {
        val shadedClasses = project.layout.buildDirectory.file("intermediates/bigaar_${it.name}/shaded_classes.jar")

        val shadeInputsTask = project.tasks.register("shade${it.name.capped}", CreateShadeInputs::class.java) { inputs ->
          inputs.shadedClassesJar.set(shadedClasses)

          inputs.outputMap.set(mappingFiles.getOrPut(it.name) { project.layout.buildDirectory
            .file("intermediates/bigaar_${it.name}/mapping.txt") })

          inputs.shadeFiles = shadedConfig

          inputs.ignorePrefixes.set(extension.ignorePrefixes)

          inputs.repackagePrefix.set(extension.repackagePrefix)
        }

        it.registerPostJavacGeneratedBytecode(project.files(shadedClasses).builtBy(shadeInputsTask))
      }
    }

    val apiExt = project.extensions.getByType(LibraryAndroidComponentsExtension::class.java)
    apiExt.onVariants {
      it.transformClassesWith(BigAarRemapperFactory::class.java, InstrumentationScope.PROJECT) { params ->
        params.remappingFile.set(mappingFiles.getOrPut(it.name) { project.layout.buildDirectory
          .file("intermediates/bigaar_${it.name}/mapping.txt") })
      }
    }
  }
}
