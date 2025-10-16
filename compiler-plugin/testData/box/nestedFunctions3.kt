// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("Fail", 42)
  with("OK") {
    return contextOf<String>()
  }
}