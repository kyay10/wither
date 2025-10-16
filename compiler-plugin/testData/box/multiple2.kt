// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("Fail", 42)
  with(42, "OK")
  return contextOf<String>()
}