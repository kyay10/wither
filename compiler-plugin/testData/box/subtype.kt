// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("OK")
  return contextOf<CharSequence>().toString()
}