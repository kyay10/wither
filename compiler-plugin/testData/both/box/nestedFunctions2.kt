fun box(): String {
  defaultWither("OK", 42)
  with(Unit) {
    return contextOf<String>()
  }
}