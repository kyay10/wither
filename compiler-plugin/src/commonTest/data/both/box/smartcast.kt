import kotlin.contracts.*

fun box(): String {
  defaultWither("OK" as Any)
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