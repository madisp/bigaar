package pink.madis.bigaar

import java.util.Locale

val String.capped: String get() = replaceFirstChar {
  if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
}
