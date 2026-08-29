fun box(): String {
  defaultWither("Fail", 42)
  with("OK") {
    return contextOf<String>()
  }
}