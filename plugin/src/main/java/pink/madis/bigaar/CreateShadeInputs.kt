package pink.madis.bigaar

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.Usage.JAVA_RUNTIME
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

abstract class CreateShadeInputs : DefaultTask() {
  @get:OutputFile
  abstract val shadedClassesJar: RegularFileProperty

  @get:InputFiles
  lateinit var shadeFiles: Configuration

  @get:Input
  abstract val ignorePrefixes: ListProperty<String>

  @get:Input
  abstract val repackagePrefix: Property<String>

  @get:OutputFile
  abstract val outputMap: RegularFileProperty

  @TaskAction fun run() {
    val outJar = shadedClassesJar.asFile.get()
    val namesMap = outputMap.asFile.get()

    val ignores = ignorePrefixes.get().map { it.replace('.', '/') }
    val prefix = repackagePrefix.get().replace('.', '/')

    val artifactView = shadeFiles.incoming.artifactView {
      it.attributes {
        it.attribute(
          Usage.USAGE_ATTRIBUTE, project.objects.named(
            Usage::class.java,
            Usage.JAVA_RUNTIME
          ))
      }
    }

    namesMap.writer(charset = Charsets.UTF_8).use { names ->
      ZipOutputStream(outJar.outputStream().buffered()).use { outStream ->

        artifactView.files.forEach {
          if (!it.isFile) {
            throw IllegalStateException("Only file inputs (jars/aars) are accepted for shading")
          }

          if (it.extension != "jar") {
            throw IllegalStateException("Libraries of type ${it.extension} are not supported at this time")
          }

          ZipInputStream(it.inputStream().buffered()).use { inStream ->
            var entry = inStream.nextEntry
            while (entry != null) {
              // only copy classes, META-INF will be lost
              if (entry.name.endsWith(".class", ignoreCase = true) && !entry.name.endsWith("module-info.class")) {
                var newName = entry.name
                if (ignores.none(entry.name::startsWith)) {
                  newName = "$prefix/${entry.name}"
                  // write the new name down
                  names.write("${entry.name.removeSuffix(".class")} -> ${newName.removeSuffix(".class")}\n")
                }

                val outEntry = ZipEntry(newName)
                // only storing is fine, this task will be cached anyway for most of the time
                outEntry.method = ZipEntry.STORED
                outEntry.size = entry.size
                outEntry.crc = entry.crc
                outStream.putNextEntry(outEntry)
                inStream.copyTo(outStream)
                outStream.closeEntry()
              }
              entry = inStream.nextEntry
            }
          }
        }
      }
    }
  }
}
