// LANGUAGE: +ContextParameters

import kotlin.contracts.*
import io.github.kyay10.wither.*

fun box(): String {
  with("OK" as Any)
  smartCastContext()
  return contextOf<String>()
}

@OptIn(ExperimentalContracts::class)
context(x: Any)
fun smartCastContext() {
  contract {
    returns() implies (x is String)
  }
  x as String
}