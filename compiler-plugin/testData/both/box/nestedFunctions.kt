fun box(): String {
  with(Unit) {
    defaultWither("OK", 42)
    return contextOf<String>()
  }
}