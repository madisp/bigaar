package pink.madis.bigaar

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper

@Suppress("UnstableApiUsage")
abstract class BigAarRemapperFactory : AsmClassVisitorFactory<RemapperParams> {
  override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
    val mappingFile = parameters.get().remappingFile.asFile.get()
    val map = mappingFile.readLines().filter { it.isNotBlank() }.associate {
        val (oldName, newName) = it.trim().split(" -> ", limit = 2)
        oldName to newName
      }

    return ClassRemapper(nextClassVisitor, BigAarRemapper(map))
  }

  override fun isInstrumentable(classData: ClassData): Boolean {
    return true
  }
}

class BigAarRemapper(
  private val mapping: Map<String, String>
) : Remapper() {
  override fun map(internalName: String): String {
    val newName = mapping[internalName] ?: super.map(internalName)
    println("Mapped $internalName to $newName")
    return newName
  }
}

interface RemapperParams : InstrumentationParameters {
  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:InputFile
  val remappingFile: RegularFileProperty
}
