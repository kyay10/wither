// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("OK")
  return getString()
}

context(x: T)
fun <T: String> getString() = x