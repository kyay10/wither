fun box(): String {
  defaultWither("NO", 0)
  if (true) {
    defaultWither("OK", 42)
    return contextOf<String>()
  }
  return contextOf<String>()
}