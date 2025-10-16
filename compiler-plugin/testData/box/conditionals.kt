// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  if (true) {
    with("OK", 42)
    return contextOf<String>()
  }
  else error("inaccessible")
}