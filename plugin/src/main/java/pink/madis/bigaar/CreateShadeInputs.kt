package pink.madis.bigaar

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.RegularFileProperty
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

  @TaskAction fun run() {
    val outJar = shadedClassesJar.asFile.get()

    ZipOutputStream(outJar.outputStream().buffered()).use { outStream ->
      (shadeFiles.files).forEach {
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
            if (entry.name.endsWith(".class", ignoreCase = true)) {
              val outEntry = ZipEntry(entry.name)
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
