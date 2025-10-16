// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("OK", 42)
  return contextOf<String>()
}