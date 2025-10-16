// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("OK", 42)
  with(Unit) {
    return contextOf<String>()
  }
}