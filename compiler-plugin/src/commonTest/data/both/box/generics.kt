fun box(): String {
  defaultWither("OK")
  return getString()
}

context(x: T)
fun <T: String> getString() = x