package pink.madis.bigaar

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface BigAarExtension {
  val ignorePrefixes: ListProperty<String>

  val repackagePrefix: Property<String>
}
